import type { Ref } from 'vue'
import 'vue-router'

export type Arrayable<T> = T | T[]
export type PromiseOrNoFn<T> = T | Promise<T>
// export type Refable<T> = Ref<T> | T  MaybeRef

declare module 'vue-router' {
  interface RouteMeta {
    menuIndex?: string
    tabIndex?: string
    tabName?: string
  }
}
