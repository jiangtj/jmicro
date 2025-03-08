<script setup lang="ts">
import { reactive, ref } from 'vue'
import { nonNull } from '@/core/form'
import type { ComponentSize, FormInstance, FormRules } from 'element-plus'

const form = reactive({
  user: '',
  region: '',
  date: ''
})

const options = ['Option 1', 'Option 2', 'Option 3']

const submitForm = () => {
  console.log('submit!', form)
}

const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive({
  name: 'Hello',
  region: ''
})

const locationOptions = ['Home', 'Company', 'School']
const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input Activity name', trigger: 'blur' },
    { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' }
  ],
  region: [
    {
      required: true,
      message: 'Please select Activity zone',
      trigger: 'change'
    }
  ]
})

const submitForm2 = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!')
    } else {
      console.log('error submit!', fields)
    }
  })
}
</script>

<template>
  <p>通过 v-slot validate 校验</p>
  <MainForm v-slot="{ validate }" :model="form" status-icon>
    <MainFormItem label="用户名" prop="user" :rules="[nonNull()]">
      <MainInput v-model="form.user" />
    </MainFormItem>
    <MainFormItem label="地区" prop="region" :rules="nonNull()">
      <MainSelect v-model="form.region" :options="options" />
    </MainFormItem>
    <el-form-item>
      <el-button type="primary" @click="validate(submitForm)">Create</el-button>
      <el-button>Cancel</el-button>
    </el-form-item>
  </MainForm>
  <el-divider />

  <p>el 校验例子</p>
  <el-form
    ref="ruleFormRef"
    style="max-width: 600px"
    :model="ruleForm"
    :rules="rules"
    label-width="auto"
    class="demo-ruleForm"
    status-icon
  >
    <el-form-item label="Activity name" prop="name">
      <el-input v-model="ruleForm.name" />
    </el-form-item>
    <el-form-item label="Activity zone" prop="region">
      <el-select v-model="ruleForm.region" placeholder="Activity zone">
        <el-option label="Zone one" value="shanghai" />
        <el-option label="Zone two" value="beijing" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm2(ruleFormRef)"> Create </el-button>
    </el-form-item>
  </el-form>
  <el-divider />

  <p>el 校验例子(item rules)</p>
  <el-form
    ref="ruleFormRef"
    style="max-width: 600px"
    :model="ruleForm"
    label-width="auto"
    class="demo-ruleForm"
    status-icon
  >
    <el-form-item
      label="Activity name"
      prop="name"
      :rules="{ required: true, message: 'Please input Activity name', trigger: 'blur' }"
    >
      <el-input v-model="ruleForm.name" />
    </el-form-item>
    <el-form-item
      label="Activity zone"
      prop="region"
      :rules="{
        required: true,
        message: 'Please select Activity zone',
        trigger: 'change'
      }"
    >
      <el-select v-model="ruleForm.region" placeholder="Activity zone">
        <el-option label="Zone one" value="shanghai" />
        <el-option label="Zone two" value="beijing" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm2(ruleFormRef)"> Create </el-button>
    </el-form-item>
  </el-form>
  <el-divider />
</template>
