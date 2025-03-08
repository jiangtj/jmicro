import { ref, computed } from 'vue'
import type { Ref } from 'vue'
import { useLocalStorage } from '@vueuse/core'
import axios from 'axios'
import type { InternalAxiosRequestConfig, AxiosResponse } from 'axios'

export type HttpMethod = 'get' | 'post' | 'put' | 'delete'

export interface HttpOptions {
  name: string
  baseUrl?: string
  request?: (method: HttpMethod, url: string, data?: any, config?: any) => Promise<any>
}

export interface PageParams {
  page?: number
  size?: number
}

export interface SortParams {
  sortQ?: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
}

export type Pageable<T> = Partial<T> & PageParams

export const env = import.meta.env.VITE_HTTP_ENV_CACHE
  ? useLocalStorage('http-env', import.meta.env.VITE_HTTP_ENV)
  : ref(import.meta.env.VITE_HTTP_ENV)

export const options: HttpOptions[] = []

export const option = computed(() => {
  return options.find((o) => o.name === env.value) ?? options[0]
})

export const defineBaseUrlOptions = (...o: HttpOptions[]) => {
  options.push(...o)
}

type PromiseOrNoFn<V> = (value: V) => V | Promise<V>

interface AxiosRequestInterceptor {
  order?: number
  config: PromiseOrNoFn<InternalAxiosRequestConfig> | null
  onRejected?: (error: any) => any
}

interface AxiosResponseInterceptor {
  order?: number
  config: PromiseOrNoFn<AxiosResponse> | null
  onRejected?: (error: any) => any
}

const requestInterceptors: Ref<AxiosRequestInterceptor[]> = ref([])
const responseInterceptors: Ref<AxiosResponseInterceptor[]> = ref([])

export const defineRequestInterceptor = (
  interceptor: AxiosRequestInterceptor | PromiseOrNoFn<InternalAxiosRequestConfig>,
  order?: number
) => {
  const temp = interceptor as AxiosRequestInterceptor
  if (temp.config) {
    if (order) {
      temp.order = order
    }
    requestInterceptors.value.push(temp)
    return
  }
  requestInterceptors.value.push({
    config: interceptor as PromiseOrNoFn<InternalAxiosRequestConfig>,
    order
  })
}

export const defineResponseInterceptor = (
  interceptor: AxiosResponseInterceptor | PromiseOrNoFn<AxiosResponse>,
  order?: number
) => {
  const temp = interceptor as AxiosResponseInterceptor
  if (temp.config) {
    if (order) {
      temp.order = order
    }
    responseInterceptors.value.push(temp)
    return
  }
  responseInterceptors.value.push({
    config: interceptor as PromiseOrNoFn<AxiosResponse>,
    order
  })
}

export const client = computed(() => {
  const inst = axios.create({
    baseURL: option.value.baseUrl,
    timeout: 10000
  })
  if (requestInterceptors.value.length > 0) {
    requestInterceptors.value
      .sort((a, b) => (a.order ?? 999) - (b.order ?? 999))
      .forEach((i) => {
        inst.interceptors.request.use(i.config, i.onRejected)
      })
  }
  if (responseInterceptors.value.length > 0) {
    responseInterceptors.value
      .sort((a, b) => (a.order ?? 999) - (b.order ?? 999))
      .forEach((i) => {
        inst.interceptors.response.use(i.config, i.onRejected)
      })
  }
  return inst
})

export const request = <T>(method: HttpMethod, url: string, data?: any, config?: any) => {
  if (option.value.request) {
    return <T>option.value.request(method, url, data, config)
  }
  return <T>client.value.request({ method, url, data, ...config })
}

export interface MockContent {
  method: HttpMethod
  url: string
  data?: any
  config?: any
}

export const mockData: Record<HttpMethod, Record<string, (ctx: MockContent) => Promise<any>>> = {
  get: {},
  post: {},
  put: {},
  delete: {}
}
export const addMockData = (
  method: HttpMethod,
  url: string,
  data: (ctx: MockContent) => Promise<any>
) => {
  mockData[method][url] = data
}
export const mock = (method: HttpMethod, url: string, data?: any, config?: any) => {
  return mockData[method][url]({ method, url, data, config })
}

const modules: Record<string, any> = import.meta.glob(['@/mock/*.ts'], {
  eager: true
})
for (const path in modules) {
  modules[path].default.install()
}
