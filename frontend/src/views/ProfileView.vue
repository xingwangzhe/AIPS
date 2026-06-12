<template>
  <div>
    <AppHeader title="个人中心" />
    <div class="profile-card">
      <div class="avatar">👤</div>
      <div>
        <template v-if="auth.isLoggedIn">
          <h3 style="font-size:18px;font-weight:600;margin:0">{{ auth.nickname }}</h3>
          <p style="font-size:14px;color:#9ca3af;margin:2px 0 0">会员等级 · 普通</p>
        </template>
        <template v-else>
          <h3 style="font-size:18px;color:#9ca3af;margin:0">未登录</h3>
          <el-button size="small" type="primary" style="margin-top:8px" @click="$router.push('/login')">立即登录</el-button>
        </template>
      </div>
    </div>

    <div v-if="auth.isLoggedIn" class="section-card">
      <h3>健康档案</h3>
      <div v-if="health.allergyInfo" style="margin-bottom:12px"><span style="color:#9ca3af;font-size:12px">过敏信息：</span><span style="color:#ef4444;font-size:14px">{{ health.allergyInfo }}</span></div>
      <div v-if="health.chronicDiseases" style="margin-bottom:12px"><span style="color:#9ca3af;font-size:12px">慢性病史：</span><span style="color:#f59e0b;font-size:14px">{{ health.chronicDiseases }}</span></div>
      <div v-if="health.latestBloodPressure" style="display:flex;gap:12px;margin:12px 0">
        <div class="vital-box"><span style="color:#9ca3af;font-size:12px">收缩压</span><span style="font-size:22px;font-weight:700;color:#3b82f6">{{ health.latestBloodPressure.systolic }}</span></div>
        <div class="vital-box"><span style="color:#9ca3af;font-size:12px">舒张压</span><span style="font-size:22px;font-weight:700;color:#3b82f6">{{ health.latestBloodPressure.diastolic }}</span></div>
      </div>
      <div v-if="health.latestBloodGlucose" class="vital-box"><span style="color:#9ca3af;font-size:12px">血糖（{{ health.latestBloodGlucose.type }}）</span><span style="font-size:22px;font-weight:700;color:#a855f7">{{ health.latestBloodGlucose.value }}</span></div>
      <div v-if="health.currentMedications?.length" style="margin-top:16px">
        <h4 style="font-size:14px;font-weight:500;color:#374151;margin:0 0 8px">当前用药</h4>
        <div v-for="(m,i) in health.currentMedications" :key="i" style="display:flex;justify-content:space-between;font-size:14px;padding:4px 0"><span>{{ m.name }}</span><span style="color:#9ca3af">{{ m.dosage }}</span></div>
      </div>
      <div v-if="health.familyMembers?.length" style="margin-top:16px">
        <h4 style="font-size:14px;font-weight:500;color:#374151;margin:0 0 8px">家庭成员</h4>
        <span v-for="(m,i) in health.familyMembers" :key="i" style="padding:4px 12px;background:#f3f4f6;border-radius:20px;font-size:12px;color:#666;margin-right:8px">{{ m.name }}（{{ m.relationship }}）</span>
      </div>
    </div>

    <div v-if="auth.isLoggedIn" class="section-card">
      <h3>用药提醒</h3>
      <el-form label-position="top" size="small" @submit.prevent="handleSetReminder">
        <el-form-item label="药品名称"><el-input v-model="reminder.medicineName" placeholder="如：氨氯地平片" /></el-form-item>
        <el-form-item label="用量"><el-input v-model="reminder.dosage" placeholder="如：5mg 每日1次" /></el-form-item>
        <el-form-item label="提醒时间"><el-time-picker v-model="reminder.remindTime" placeholder="选择时间" format="HH:mm" value-format="HH:mm" /></el-form-item>
        <el-button type="primary" @click="handleSetReminder">设置提醒</el-button>
      </el-form>
    </div>

    <div v-if="auth.isLoggedIn" style="padding:16px">
      <el-button type="danger" style="width:100%" @click="handleLogout">退出登录</el-button>
    </div>
    <div style="height:56px"></div>
    <BottomNav />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { getHealthProfile, setReminder } from '@/api/user.js'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import BottomNav from '@/components/BottomNav.vue'

const router = useRouter(); const auth = useAuthStore()
const health = ref({})
const reminder = reactive({ medicineId: 1, medicineName: '', dosage: '', remindTime: '', repeatDays: '1,2,3,4,5,6,7', isEnabled: 1 })

async function fetchHealth() { try { health.value = await getHealthProfile() } catch {} }
async function handleSetReminder() {
  if (!reminder.medicineName || !reminder.remindTime) return ElMessage.warning('请填写药品名称和提醒时间')
  try { await setReminder(reminder); ElMessage.success('用药提醒设置成功') } catch(e) { ElMessage.error(e.message||'设置失败') }
}
function handleLogout() { auth.logout(); router.push('/login') }
onMounted(() => { if (auth.isLoggedIn) fetchHealth() })
</script>

<style scoped>
.profile-card { margin:12px 16px;padding:20px;background:#fff;border-radius:12px;display:flex;align-items:center;gap:16px; }
.avatar { width:56px;height:56px;border-radius:50%;background:#dbeafe;display:flex;align-items:center;justify-content:center;font-size:28px; }
.section-card { margin:12px 16px;padding:16px;background:#fff;border-radius:12px; }
.section-card h3 { font-size:16px;font-weight:600;color:#1f2937;margin:0 0 12px; }
.vital-box { flex:1;background:#eff6ff;border-radius:8px;padding:12px;text-align:center;display:flex;flex-direction:column; }
</style>
