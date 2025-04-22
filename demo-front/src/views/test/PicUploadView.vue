<template>
  <div class="pic-upload-container">
    <h2>图片上传</h2>

    <div class="upload-area">
      <el-upload
        class="upload-component"
        action="custom-http-request"
        :http-request="uploadAction"
        :on-preview="handlePreview"
        :on-success="handleSuccess"
        :on-error="handleError"
        :before-upload="beforeUpload"
        :on-progress="onProgress"
        :file-list="fileList"
        list-type="picture-card"
        :multiple="false"
      >
        <el-icon><Plus /></el-icon>
      </el-upload>
    </div>

    <div v-if="uploadProgress > 0 && uploadProgress < 100" class="progress-area">
      <el-progress :percentage="uploadProgress" :stroke-width="15" status="success" />
    </div>

    <div v-if="uploadResult" class="result-area">
      <h3>上传结果</h3>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="原始文件名">{{
          uploadResult.originalFileName
        }}</el-descriptions-item>
        <el-descriptions-item label="新文件名">{{ uploadResult.fileName }}</el-descriptions-item>
        <el-descriptions-item label="文件路径">{{ uploadResult.filePath }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{
          formatFileSize(uploadResult.fileSize)
        }}</el-descriptions-item>
        <el-descriptions-item label="文件类型">{{ uploadResult.fileType }}</el-descriptions-item>
        <el-descriptions-item v-if="uploadResult.thumbnailPath" label="缩略图路径">
          {{ uploadResult.thumbnailPath }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <el-dialog v-model="previewVisible" title="图片预览">
      <img width="100%" :src="previewUrl" alt="Preview Image" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Plus from '~icons/ep/Plus'
import type { UploadFile, UploadProps } from 'element-plus'
import { ElMessage } from 'element-plus'
import { request } from '@/core/http-client'

interface PicUploadResult {
  originalFileName: string
  fileName: string
  filePath: string
  fileSize: number
  fileType: string
  thumbnailPath?: string
}

const fileList = ref<UploadFile[]>([])
const uploadProgress = ref(0)
const uploadResult = ref<PicUploadResult | null>(null)
const previewVisible = ref(false)
const previewUrl = ref('')

const handlePreview = (file: UploadFile) => {
  previewUrl.value = file.url || ''
  previewVisible.value = true
}

const handleSuccess = (response: PicUploadResult, file: UploadFile) => {
  uploadProgress.value = 100
  uploadResult.value = response
  ElMessage.success('上传成功')
}

const handleError = () => {
  uploadProgress.value = 0
  ElMessage.error('上传失败')
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }

  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }

  uploadProgress.value = 0
  uploadResult.value = null
  return true
}

const onProgress: UploadProps['onProgress'] = (event) => {
  uploadProgress.value = Math.round(event.percent)
}

const formatFileSize = (size: number) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / 1024 / 1024).toFixed(2) + ' MB'
  }
}

const uploadAction: UploadProps['httpRequest'] = (data) => {
  const file = data.file
  const forms = new FormData() // 实例化一个formData，用来做文件上传
  forms.append('file', file)
  forms.append('target', 'test')
  // api.upload(forms).then((data) => {
  //   emit('update:modelValue', data);
  // });
  return request<any>('post', '/pic/upload', forms, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then((data: any) => {
    // model.value = data
    console.log(data)
  })
}
</script>

<style scoped>
.pic-upload-container {
  padding: 20px;
}

.upload-area {
  margin: 20px 0;
}

.progress-area {
  margin: 20px 0;
}

.result-area {
  margin: 20px 0;
  max-width: 800px;
}
</style>