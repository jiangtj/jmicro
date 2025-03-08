import { useLocalStorage, type RemovableRef } from '@vueuse/core'

export const useLocalSelect = <Type extends Object, Key extends keyof Type & string>(
  key: string,
  obj: Type,
  defaultValue?: Key
) => {
  const list = Object.keys(obj)
  if (!defaultValue) {
    if (list.length === 0) {
      throw new Error('obj should have property')
    }
    defaultValue = list[0] as Key
  }
  return <RemovableRef<keyof Type>>useLocalStorage(key, defaultValue, {
    serializer: {
      read: (raw: string) => {
        if (list.includes(raw)) {
          return raw
        }
        return defaultValue
      },
      write: (v: string) => v
    }
  })
}

export const useLocalSwitch = (key: string, defaultValue: boolean) => {
  return useLocalStorage<boolean>(key, defaultValue, {
    serializer: {
      read: (v: string) => {
        if (['true', 'false'].includes(v)) {
          return v === 'true'
        }
        return defaultValue
      },
      write: (v: boolean) => String(v)
    }
  })
}
