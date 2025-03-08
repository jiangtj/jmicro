<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import type { FormRules, FormProps } from 'element-plus'
import { defaultValidate } from '@/core/form'
import type { FormInstance, FormInstanceValidate } from '@/core/form'

const props = defineProps<{
  model?: Record<string, any>
  formProps?: Partial<FormProps>
}>()

const formRef = ref<FormInstance>()

const validate = ref<FormInstanceValidate>(defaultValidate)

watchEffect(() => {
  if (formRef.value) {
    validate.value = formRef.value.validate
  }
})

defineExpose({ form: formRef, validate })
</script>

<template>
  <MainDialog>
    <template #header><slot name="header"></slot></template>
    <MainForm :model="props.model" ref="formRef" v-bind="props.formProps">
      <slot :validate="validate"></slot>
    </MainForm>
    <template #footer><slot name="footer" :validate="validate"></slot></template>
  </MainDialog>
</template>
