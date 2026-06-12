<template>
  <div class="pb-4">
    <AppHeader title="个人中心" />

    <!-- 用户信息卡片 -->
    <div class="px-4 pt-3">
      <div class="bg-white rounded-xl p-5 shadow-sm flex items-center gap-4">
        <div class="w-14 h-14 rounded-full bg-blue-100 flex items-center justify-center text-2xl">
          👤
        </div>
        <div class="flex-1">
          <template v-if="auth.isLoggedIn">
            <h3 class="text-lg font-semibold">{{ auth.nickname }}</h3>
            <p class="text-sm text-gray-400">会员等级 · 普通</p>
          </template>
          <template v-else>
            <h3 class="text-lg font-semibold text-gray-400">未登录</h3>
            <el-button size="small" type="primary" class="mt-2" @click="$router.push('/login')">立即登录</el-button>
          </template>
        </div>
      </div>
    </div>

    <!-- 健康档案 -->
    <div class="px-4 pt-4" v-if="auth.isLoggedIn">
      <div class="bg-white rounded-xl p-4 shadow-sm">
        <h3 class="text-base font-semibold text-gray-800 mb-3">健康档案</h3>

        <div v-if="health.allergyInfo" class="mb-3">
          <span class="text-xs text-gray-400">过敏信息：</span>
          <span class="text-sm text-red-500">{{ health.allergyInfo }}</span>
        </div>
        <div v-if="health.chronicDiseases" class="mb-3">
          <span class="text-xs text-gray-400">慢性病史：</span>
          <span class="text-sm text-orange-500">{{ health.chronicDiseases }}</span>
        </div>

        <!-- 血压 -->
        <div v-if="health.latestBloodPressure" class="flex gap-4 my-3">
          <div class="flex-1 bg-blue-50 rounded-lg p-3 text-center">
            <p class="text-xs text-gray-400">收缩压</p>
            <p class="text-xl font-bold text-blue-600">{{ health.latestBloodPressure.systolic }}</p>
          </div>
          <div class="flex-1 bg-blue-50 rounded-lg p-3 text-center">
            <p class="text-xs text-gray-400">舒张压</p>
            <p class="text-xl font-bold text-blue-600">{{ health.latestBloodPressure.diastolic }}</p>
          </div>
        </div>

        <!-- 血糖 -->
        <div v-if="health.latestBloodGlucose" class="bg-purple-50 rounded-lg p-3 text-center">
          <p class="text-xs text-gray-400">血糖（{{ health.latestBloodGlucose.type }}）</p>
          <p class="text-xl font-bold text-purple-600">{{ health.latestBloodGlucose.value }}</p>
        </div>

        <!-- 当前用药 -->
        <div v-if="health.currentMedications?.length" class="mt-4">
          <h4 class="text-sm font-medium text-gray-700 mb-2">当前用药</h4>
          <div v-for="(med, i) in health.currentMedications" :key="i"
               class="flex justify-between text-sm py-1">
            <span>{{ med.name }}</span>
            <span class="text-gray-400">{{ med.dosage }}</span>
          </div>
        </div>

        <!-- 家庭成员 -->
        <div v-if="health.familyMembers?.length" class="mt-4">
          <h4 class="text-sm font-medium text-gray-700 mb-2">家庭成员</h4>
          <div class="flex gap-2">
            <span v-for="(m, i) in health.familyMembers" :key="i"
                  class="px-3 py-1 bg-gray-100 rounded-full text-xs text-gray-600">
              {{ m.name }}（{{ m.relationship }}）
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 用药提醒 -->
    <div class="px-4 pt-4" v-if="auth.isLoggedIn">
      <div class="bg-white rounded-xl p-4 shadow-sm">
        <h3 class="text-base font-semibold text-gray-800 mb-3">用药提醒</h3>
        <el-form label-position="top" size="small" @submit.prevent="handleSetReminder">
          <el-form-item label="药品名称">
            <el-input v-model="reminder.medicineName" placeholder="如：氨氯地平片" />
          </el-form-item>
          <el-form-item label="用量">
            <el-input v-model="reminder.dosage" placeholder="如：5mg 每日1次" />
          </el-form-item>
          <el-form-item label="提醒时间">
            <el-time-picker v-model="reminder.remindTime" placeholder="选择时间" format="HH:mm" value-format="HH:mm" />
          </el-form-item>
          <el-button type="primary" @click="handleSetReminder">设置提醒</el-button>
        </el-form>
      </div>
    </div>

    <!-- 退出登录 -->
    <div class="px-4 pt-4 pb-4" v-if="auth.isLoggedIn">
      <el-button type="danger" class="w-full" @click="handleLogout">退出登录</el-button>
    </div>

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

const router = useRouter()
const auth = useAuthStore()

const health = ref({})
const reminder = reactive({
  medicineId: 1,
  medicineName: '',
  dosage: '',
  remindTime: '',
  repeatDays: '1,2,3,4,5,6,7',
  isEnabled: 1,
})

async function fetchHealth() {
  try {
    health.value = await getHealthProfile()
  } catch { /* silent */ }
}

async function handleSetReminder() {
  if (!reminder.medicineName || !reminder.remindTime) {
    ElMessage.warning('请填写药品名称和提醒时间')
    return
  }
  try {
    await setReminder(reminder)
    ElMessage.success('用药提醒设置成功')
  } catch (e) {
    ElMessage.error(e.message || '设置失败')
  }
}

function handleLogout() {
  auth.logout()
  router.push('/login')
}

onMounted(() => {
  if (auth.isLoggedIn) fetchHealth()
})
</script>
