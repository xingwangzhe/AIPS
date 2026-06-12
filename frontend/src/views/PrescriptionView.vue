<template>
  <div class="pb-4">
    <AppHeader title="处方上传" show-back />

    <div class="px-4 py-6">
      <!-- 上传区 -->
      <div v-if="!prescriptionId" class="text-center">
        <div class="bg-white rounded-2xl p-8 shadow-sm">
          <p class="text-4xl mb-4">📄</p>
          <h2 class="text-lg font-semibold text-gray-800 mb-2">上传处方</h2>
          <p class="text-sm text-gray-400 mb-6">拍照或选择处方图片，JPEG/PNG 格式</p>

          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="image/jpeg,image/png"
            @change="handleFileChange"
          >
            <el-button type="primary" size="large" class="w-full">📷 拍照或选择图片</el-button>
          </el-upload>
        </div>
      </div>

      <!-- 状态查询 -->
      <div v-else-if="status" class="bg-white rounded-2xl p-6 shadow-sm">
        <div class="text-center mb-6">
          <p class="text-3xl mb-2">{{ statusText.icon }}</p>
          <h3 class="text-lg font-semibold text-gray-800">{{ statusText.label }}</h3>
          <p class="text-sm text-gray-400 mt-1">{{ statusText.desc }}</p>
          <el-progress
            :percentage="statusProgress"
            :status="status.status === 4 ? 'success' : status.status >= 5 ? 'exception' : undefined"
            class="mt-4"
          />
        </div>

        <!-- OCR 结果 -->
        <div v-if="status.hospitalName" class="border-t border-gray-100 pt-4 mt-4">
          <div class="text-sm text-gray-600 space-y-2">
            <p><span class="text-gray-400">医院：</span>{{ status.hospitalName }}</p>
            <p><span class="text-gray-400">医生：</span>{{ status.doctorName }}</p>
            <p><span class="text-gray-400">审核意见：</span>{{ status.pharmacistComment || '待审核' }}</p>
          </div>

          <!-- 药品明细 -->
          <div v-if="status.items?.length" class="mt-4">
            <h4 class="text-sm font-medium text-gray-700 mb-2">处方药品</h4>
            <div v-for="(item, i) in status.items" :key="i"
                 class="flex justify-between text-sm py-2 border-b border-gray-50">
              <span>{{ item.medicineName }} {{ item.specification }}</span>
              <span class="text-gray-400">{{ item.dosage }} × {{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-else class="text-center py-10 text-gray-400">加载中...</div>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { uploadPrescription, getPrescriptionStatus } from '@/api/prescription.js'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'

const prescriptionId = ref(null)
const status = ref(null)

const statusText = computed(() => {
  const s = status.value?.status
  if (s === 1) return { icon: '🔍', label: 'OCR 识别中', desc: '正在提取处方信息...' }
  if (s === 2) return { icon: '🤖', label: 'AI 检查中', desc: '正在分析配伍禁忌...' }
  if (s === 3) return { icon: '👨‍⚕️', label: '待药师审核', desc: '请耐心等待，30分钟内反馈' }
  if (s === 4) return { icon: '✅', label: '审核通过', desc: '您可以继续购药' }
  if (s === 5) return { icon: '❌', label: '审核驳回', desc: status.value?.pharmacistComment || '' }
  if (s === 6) return { icon: '📷', label: '需重新上传', desc: '请重新拍照上传' }
  return { icon: '⏳', label: '处理中', desc: '' }
})

const statusProgress = computed(() => {
  const s = status.value?.status
  if (s === 1) return 25
  if (s === 2) return 50
  if (s === 3) return 75
  if (s >= 4) return 100
  return 0
})

async function handleFileChange(file) {
  try {
    ElMessage.info('上传中...')
    const resp = await uploadPrescription(file.raw)
    prescriptionId.value = resp.prescriptionId
    ElMessage.success('上传成功')

    // 轮询状态
    const timer = setInterval(async () => {
      try {
        const s = await getPrescriptionStatus(prescriptionId.value)
        status.value = s
        if (s.status >= 4) {
          clearInterval(timer)
        }
      } catch { /* ignore */ }
    }, 3000)
    setTimeout(() => clearInterval(timer), 120000) // 最多轮询 2 分钟
  } catch (e) {
    ElMessage.error('上传失败')
  }
}
</script>
