import { describe, it, expect } from 'vitest'
import MockAdapter from 'axios-mock-adapter'

import { option, client, defineRequestInterceptor, defineBaseUrlOptions, env } from '../http-client'

defineRequestInterceptor((config) => {
  if (config.url === '/token-fail') {
    return Promise.reject('token is null')
  }
  return config
})

defineBaseUrlOptions({
  name: 'test',
  baseUrl: 'http://test.com'
})

describe('Http Client Tests', () => {
  it('test base url', () => {
    env.value = 'test'
    expect(client.value.defaults.baseURL).eq('http://test.com')
  })

  it('test request interceptor', async () => {
    const mock = new MockAdapter(client.value)
    mock.onGet('/users').reply(200, {
      users: [{ id: 1, name: 'John Smith' }]
    })
    mock.onGet('/token-fail').reply(200, {
      users: [{ id: 2, name: 'John Smith' }]
    })
    try {
      await client.value.get('/token-fail')
    } catch (error) {
      expect(error).eq('token is null')
    }
    const data = await client.value.get('/users')
    expect(data.data).deep.eq({
      users: [{ id: 1, name: 'John Smith' }]
    })
  })
})
