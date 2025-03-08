import { isRef, ref, unref, watch } from 'vue'
import type { MaybeRef, ModelRef, Ref } from 'vue'
import type { Arrayable } from '@vueuse/core'
import type { ValidateFieldsError } from 'async-validator'
import type {
  FormRules,
  FormItemRule,
  FormInstance as ElFormInstance,
  FormValidationResult
} from 'element-plus'

export type FormInstanceValidate = (
  success?: () => void,
  fail?: (fields: ValidateFieldsError | undefined) => void
) => Promise<void>

export const defaultValidate: FormInstanceValidate = async (success?) => {
  if (success) {
    success()
  }
}

export interface FormInstance {
  el: Ref<ElFormInstance | undefined>
  validate: FormInstanceValidate
}

export class ValidateError extends Error {
  fields: ValidateFieldsError | undefined
  constructor(fields?: ValidateFieldsError) {
    super('Validate Fail')
    this.fields = fields
  }
}

type FormItemRuleFn = (label: string) => FormItemRule

export interface FormItem {
  label: string
  tip?: string
  rules?: Arrayable<FormItemRule | FormItemRuleFn>
}

export interface SelectOption {
  label: string | number
  value: string
}

export type SelectOptions =
  | SelectOption[]
  | Record<string, string>
  | string[]
  | Map<string | number, string>

export const useValidator = (props: FormItem) => {
  let rules: Ref<Arrayable<FormItemRule> | undefined> = ref()
  if (props.rules) {
    if (Array.isArray(props.rules)) {
      rules.value = props.rules.map((rule) => {
        if (typeof rule === 'function') {
          return rule(props.label)
        }
        return rule
      })
    } else {
      if (typeof props.rules === 'function') {
        rules.value = props.rules(props.label)
      } else {
        rules.value = props.rules
      }
    }
  }
  return {
    rules
  }
}

interface FormItemRuleFnOverride {
  message?: (label: string) => string
  trigger?: string
}

export const nonNull = (config?: FormItemRuleFnOverride) => {
  let { message, trigger } = Object.assign(
    { message: (v: string) => `请输入${v}`, trigger: 'blur' },
    config
  )
  return (label: string) => ({ required: true, message: message(label), trigger })
}

export type CreateRefWithDefaultValueConfig<T> = {
  undefinedValue?: T | undefined
  isUndefined?: (v: T | undefined) => boolean
}
export const createRefWithDefaultValue = <T>(
  model: ModelRef<T | undefined>,
  defaultV?: MaybeRef<T>,
  config?: CreateRefWithDefaultValueConfig<T>
): Ref<T | undefined> => {
  if (unref(defaultV) === undefined) {
    return model
  }
  const result = ref<T>()
  const undefinedValue = config?.undefinedValue
  const isUndefined = config?.isUndefined ?? ((v) => v === undefined)

  result.value = undefinedValue

  const watchModel = (v: T | undefined) => {
    if (isUndefined(v)) {
      model.value = unref(defaultV)
      result.value = undefinedValue
      return
    }
    if (v === unref(defaultV)) {
      if (result.value !== unref(defaultV)) {
        result.value = undefinedValue
      }
      return
    }
    result.value = v
  }

  const watchResult = (v: T | undefined) => {
    if (isUndefined(v)) {
      model.value = unref(defaultV)
      return
    }
    model.value = v
  }

  watch(model, watchModel, { immediate: true })

  watch(result, watchResult)

  if (isRef(defaultV)) {
    watch(defaultV, () => {
      watchResult(result.value)
      watchModel(model.value)
    })
  }
  return result
}
