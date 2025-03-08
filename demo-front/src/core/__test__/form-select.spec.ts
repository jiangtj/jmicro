import { describe, it, expect } from 'vitest'
import { useOptions } from '../form-select'
import { reactive, ref } from 'vue'
import type { SelectOptions } from '../form'

describe('From Select Tests', () => {
  it('test useOptions value', () => {
    expect(useOptions([{ label: '1', value: '1' }]).value).toEqual([{ label: '1', value: '1' }])
    expect(useOptions(new Map([['1', '1']])).value).toEqual([{ label: '1', value: '1' }])
    expect(useOptions(new Map([[1, '1']])).value).toEqual([{ label: '1', value: 1 }])
    expect(useOptions({ 1: '1' }).value).toEqual([{ label: '1', value: '1' }])
    expect(useOptions({ 1: '1' }, true).value).toEqual([{ label: '1', value: 1 }])
  })

  it('test useOptions ref', () => {
    const options = ref<SelectOptions>([{ label: '1', value: '1' }])
    const check = useOptions(options)
    expect(check.value).toEqual([{ label: '1', value: '1' }])

    options.value = { 1: '1', 2: '2' }
    expect(check.value).toEqual([
      { label: '1', value: '1' },
      { label: '2', value: '2' }
    ])
    expect(useOptions(options, true).value).toEqual([
      { label: '1', value: 1 },
      { label: '2', value: 2 }
    ])
  })

  it('test useOptions getter', () => {
    const options = reactive<{ r: SelectOptions }>({ r: [{ label: '1', value: '1' }] })
    const check = useOptions(() => options.r)
    expect(check.value).toEqual([{ label: '1', value: '1' }])

    options.r = { 1: '1', 2: '2' }
    expect(check.value).toEqual([
      { label: '1', value: '1' },
      { label: '2', value: '2' }
    ])
    expect(useOptions(() => options.r, true).value).toEqual([
      { label: '1', value: 1 },
      { label: '2', value: 2 }
    ])
  })
})
