import { computed, toValue, type MaybeRefOrGetter } from 'vue'
import type { SelectOptions } from './form'

export const useOptions = (options: MaybeRefOrGetter<SelectOptions>, number = false) => {
  return computed(() => {
    const o = toValue(options)
    if (Array.isArray(o)) {
      return o.map((item, index) => {
        if (typeof item === 'string') {
          return {
            label: item,
            value: index
          }
        }
        return item
      })
    }
    if (o instanceof Map) {
      return Array.from(o.entries()).map(([key, value]) => {
        return {
          label: value,
          value: key
        }
      })
    }
    return Object.keys(o).map((key) => {
      return {
        label: o[key],
        value: number ? Number(key) : key
      }
    })
  })
}

// export const useOptions = (options: MaybeRefOrGetter<SelectOptions>, number = false) => {
//   const optionsRef = ref<SelectOption[]>([])
//   watchEffect(
//     () => {
//       const o = toValue(options)
//       if (Array.isArray(o)) {
//         optionsRef.value = o.map((item, index) => {
//           if (typeof item === 'string') {
//             return {
//               label: item,
//               value: index
//             }
//           }
//           return item
//         })
//         return
//       }
//       if (o instanceof Map) {
//         optionsRef.value = Array.from(o.entries()).map(([key, value]) => {
//           return {
//             label: value,
//             value: key
//           }
//         })
//         return
//       }
//       optionsRef.value = Object.keys(o).map((key) => {
//         return {
//           label: o[key],
//           value: number ? Number(key) : key
//         }
//       })
//     },
//     {
//       flush: 'sync'
//     }
//   )
//   return optionsRef
// }
