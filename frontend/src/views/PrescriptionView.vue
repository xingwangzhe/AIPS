<template>
  <div>
    <AppHeader title="处方上传" show-back />
    <div style="padding:24px 16px">
      <div v-if="!prescriptionId" class="upload-card">
        <p style="font-size:48px;margin:0">📄</p>
        <h2 style="font-size:18px;font-weight:600;color:#1f2937;margin:12px 0 0">上传处方</h2>
        <p style="font-size:14px;color:#9ca3af;margin:4px 0 24px">拍照或选择处方图片，JPEG/PNG 格式</p>
        <el-upload :auto-upload="false" :show-file-list="false" accept="image/jpeg,image/png" @change="handleFileChange">
          <el-button type="primary" size="large" style="width:100%">📷 拍照或选择图片</el-button>
        </el-upload>
      </div>
      <div v-else-if="status" class="upload-card">
        <div style="text-align:center;margin-bottom:24px">
          <p style="font-size:36px;margin:0">{{ statusIcon }}</p>
          <h3 style="font-size:18px;font-weight:600;color:#1f2937;margin:8px 0 0">{{ statusLabel }}</h3>
          <p style="font-size:14px;color:#9ca3af;margin:4px 0 0">{{ statusDesc }}</p>
          <el-progress :percentage="progress" :status="status.status === 4 ? 'success' : status.status >= 5 ? 'exception' : undefined" style="margin-top:16px" />
        </div>
        <div v-if="status.hospitalName" style="border-top:1px solid #f3f4f6;padding-top:16px;font-size:14px;color:#666">
          <p><span style="color:#9ca3af">医院：</span>{{ status.hospitalName }}</p>
          <p><span style="color:#9ca3af">医生：</span>{{ status.doctorName }}</p>
          <p><span style="color:#9ca3af">审核意见：</span>{{ status.pharmacistComment || '待审核' }}</p>
          <div v-if="status.items?.length" style="margin-top:16px">
            <h4 style="font-size:14px;font-weight:500;color:#374151;margin:0 0 8px">处方药品</h4>
            <div v-for="(item,i) in status.items" :key="i" style="display:flex;justify-content:space-between;font-size:14px;padding:6px 0;border-bottom:1px solid #f9fafb">
              <span>{{ item.medicineName }} {{ item.specification }}</span>
              <span style="color:#9ca3af">{{ item.dosage }} × {{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else style="text-align:center;padding:48px;color:#9ca3af">加载中...</div>
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

const prescriptionId = ref(null); const status = ref(null)
const statusIcon = computed(() => { const s=status.value?.status; if(s===1)return'🔍';if(s===2)return'🤖';if(s===3)return'👨‍⚕️';if(s===4)return'✅';if(s===5)return'❌';return'⏳' })
const statusLabel = computed(() => { const s=status.value?.status; if(s===1)return'OCR 识别中';if(s===2)return'AI 检查中';if(s===3)return'待药师审核';if(s===4)return'审核通过';if(s===5)return'审核驳回';return'处理中' })
const statusDesc = computed(() => { const s=status.value?.status; if(s===3)return'请耐心等待，30分钟内反馈';if(s===4)return'您可以继续购药';return status.value?.pharmacistComment||'' })
const progress = computed(() => { const s=status.value?.status; if(s===1)return 25; if(s===2)return 50; if(s===3)return 75; if(s>=4)return 100; return 0 })

async function handleFileChange(file) {
  try { ElMessage.info('上传中...'); const resp = await uploadPrescription(file.raw); prescriptionId.value = resp.prescriptionId; ElMessage.success('上传成功')
    const timer = setInterval(async () => { const s = await getPrescriptionStatus(prescriptionId.value); status.value = s; if(s.status >= 4) clearInterval(timer) }, 3000)
    setTimeout(() => clearInterval(timer), 120000)
  } catch { ElMessage.error('上传失败') }
}
</script>

<style scoped>
.upload-card { background: #fff; border-radius: 16px; padding: 32px; text-align: center; }
</style>
