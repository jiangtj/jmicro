import { addMockData } from '@/core/http-client'
import avaterUrl from '@/assets/avater.jpg'

const superUser = { nick: 'Super Admin', subject: 1, avater: avaterUrl }

export default {
  install: () => {
    addMockData('post', '/login', () => {
      return Promise.resolve({
        user: superUser,
        token:
          'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjI4MTYyMzkwMjJ9.D3OHS3FQztnFUckHo-B-HF20aic_SWfUxG-Hy_lNNhQ'
      })
    })
    addMockData('get', '/user', () => {
      return Promise.resolve(superUser)
    })
  }
}
