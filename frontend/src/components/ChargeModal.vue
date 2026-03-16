<template>
  <Teleport to="body">
    <transition name="fade">
      <div v-if="isOpen" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full animate-scale-in">
          <!-- Header -->
          <div class="bg-gradient-to-r from-primary-500 to-primary-600 text-white p-6 rounded-t-2xl">
            <h2 class="text-2xl font-bold">⚡ 选择充电方案</h2>
            <p class="text-primary-100 mt-1">选择充电类型和时长</p>
          </div>

          <!-- Content -->
          <div class="p-6 space-y-6">
            <!-- 充电类型选择 -->
            <div>
              <label class="block text-sm font-semibold text-gray-900 mb-3">充电类型</label>
              <div class="grid grid-cols-3 gap-3">
                <button
                  v-for="type in chargeTypes"
                  :key="type.value"
                  @click="selectedType = type.value"
                  :class="[
                    'p-4 rounded-lg border-2 transition-all duration-200 text-center',
                    selectedType === type.value
                      ? 'border-primary-500 bg-primary-50 shadow-md'
                      : 'border-gray-200 bg-gray-50 hover:border-gray-300'
                  ]"
                >
                  <div class="text-2xl mb-2">{{ type.icon }}</div>
                  <p class="font-semibold text-sm text-gray-900">{{ type.label }}</p>
                  <p class="text-xs text-gray-600 mt-1">{{ type.desc }}</p>
                </button>
              </div>
            </div>

            <!-- 充电时长选择 -->
            <div>
              <label class="block text-sm font-semibold text-gray-900 mb-3">充电时长</label>
              <div class="space-y-2">
                <div class="flex items-center space-x-2">
                  <input
                    type="range"
                    v-model.number="selectedTime"
                    min="1"
                    max="8"
                    step="1"
                    class="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-primary-500"
                  />
                  <span class="text-lg font-bold text-primary-600 min-w-[60px] text-right">{{ selectedTime }}小时</span>
                </div>
                <div class="flex justify-between text-xs text-gray-500">
                  <span>1小时</span>
                  <span>8小时</span>
                </div>
              </div>

              <!-- 快速选择按钮 -->
              <div class="grid grid-cols-4 gap-2 mt-4">
                <button
                  v-for="time in [1, 2, 4, 8]"
                  :key="time"
                  @click="selectedTime = time"
                  :class="[
                    'py-2 px-3 rounded-lg font-semibold text-sm transition-all duration-200',
                    selectedTime === time
                      ? 'bg-primary-500 text-white shadow-md'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  ]"
                >
                  {{ time }}h
                </button>
              </div>
            </div>

            <!-- 费用预估 -->
            <div class="bg-gradient-to-r from-blue-50 to-green-50 p-4 rounded-lg border border-blue-200">
              <p class="text-sm text-gray-600 mb-2">预估费用</p>
              <div class="flex items-baseline space-x-2">
                <span class="text-3xl font-bold text-primary-600">¥{{ estimatedPrice.toFixed(2) }}</span>
                <span class="text-sm text-gray-600">({{ selectedTime }}小时 × {{ getPricePerHour() }}/小时)</span>
              </div>
            </div>

            <!-- 充电类型说明 -->
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-xs font-semibold text-gray-700 mb-2">💡 充电类型说明</p>
              <ul class="text-xs text-gray-600 space-y-1">
                <li>⚡ <span class="font-semibold">快充</span>: 30分钟充满，¥5/小时</li>
                <li>🔌 <span class="font-semibold">慢充</span>: 2小时充满，¥2/小时</li>
                <li>🚀 <span class="font-semibold">超快充</span>: 15分钟充满，¥8/小时</li>
              </ul>
            </div>
          </div>

          <!-- Footer -->
          <div class="flex gap-3 p-6 border-t border-gray-200 bg-gray-50 rounded-b-2xl">
            <button
              @click="handleCancel"
              class="flex-1 px-4 py-3 bg-gray-200 hover:bg-gray-300 text-gray-900 font-semibold rounded-lg transition-colors duration-200"
            >
              取消
            </button>
            <button
              @click="handleConfirm"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-primary-500 to-primary-600 hover:from-primary-600 hover:to-primary-700 text-white font-semibold rounded-lg transition-all duration-200 shadow-md hover:shadow-lg active:scale-95"
            >
              确认预约
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['confirm', 'cancel'])

const chargeTypes = [
  { value: 0, label: '快充', icon: '⚡', desc: '30分钟' },
  { value: 1, label: '慢充', icon: '🔌', desc: '2小时' },
  { value: 2, label: '超快充', icon: '🚀', desc: '15分钟' }
]

const selectedType = ref(0)
const selectedTime = ref(2)

const getPricePerHour = () => {
  const priceMap = {
    0: 5,    // 快充 ¥5/小时
    1: 2,    // 慢充 ¥2/小时
    2: 8     // 超快充 ¥8/小时
  }
  return priceMap[selectedType.value] || 0
}

const estimatedPrice = computed(() => {
  return selectedTime.value * getPricePerHour()
})

const handleConfirm = () => {
  emit('confirm', {
    chargeType: selectedType.value,
    chargeTime: selectedTime.value,
    expectedAmount: estimatedPrice.value
  })
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
@keyframes scale-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-scale-in {
  animation: scale-in 0.2s ease-out;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
