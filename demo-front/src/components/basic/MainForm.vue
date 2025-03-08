<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { ValidateFieldsError } from 'async-validator'
import type { FormInstance as ElFormInstance, FormRules } from 'element-plus'
import { ValidateError, type FormInstance } from '@/core/form'

const formRef = ref<ElFormInstance>()

const validate = async (
  success?: () => void,
  fail?: (fields: ValidateFieldsError | undefined) => void
) => {
  let fieldsResult: ValidateFieldsError | undefined
  const result = await formRef.value?.validate((valid, fields) => {
    if (valid) {
      if (success) success()
    } else {
      fieldsResult = fields
      if (fail) fail(fields)
    }
  })
  if (result) {
    return
  }
  throw new ValidateError(fieldsResult)
}

defineExpose<FormInstance>({ el: formRef, validate })
</script>

<template>
  <el-form label-width="auto" ref="formRef">
    <slot :validate="validate" />
  </el-form>
</template>

<style></style>
