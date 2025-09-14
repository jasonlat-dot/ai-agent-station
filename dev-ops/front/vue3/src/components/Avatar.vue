<template>
  <!-- 头像组件 -->
  <div 
    :class="[
      'flex items-center justify-center rounded-full overflow-hidden',
      sizeClass,
      type === 'ai' ? 'bg-gradient-to-br from-blue-500 to-purple-600' : 'bg-gradient-to-br from-green-500 to-teal-600'
    ]"
    :title="type === 'ai' ? 'AI助手' : '用户'"
  >
    <!-- AI头像 -->
    <svg 
      v-if="type === 'ai'"
      :class="iconSizeClass"
      fill="white" 
      viewBox="0 0 24 24"
    >
      <path d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 1H5C3.89 1 3 1.89 3 3V21C3 22.11 3.89 23 5 23H19C20.11 23 21 22.11 21 21V9M19 9H14V4H19V9ZM12 19C10.34 19 9 17.66 9 16S10.34 13 12 13 15 14.34 15 16 13.66 19 12 19ZM12 15C11.45 15 11 15.45 11 16S11.45 17 12 17 13 16.55 13 16 12.55 15 12 15Z"/>
    </svg>
    
    <!-- 用户头像 -->
    <svg 
      v-else
      :class="iconSizeClass"
      fill="white" 
      viewBox="0 0 24 24"
    >
      <path d="M12 12C14.21 12 16 10.21 16 8S14.21 4 12 4 8 5.79 8 8 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z"/>
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue'

/**
 * 头像组件
 * 功能：显示AI助手和用户的头像
 */

// Props
const props = defineProps({
  /**
   * 头像类型
   * @type {'ai' | 'user'}
   */
  type: {
    type: String,
    required: true,
    validator: (value) => ['ai', 'user'].includes(value)
  },
  /**
   * 头像大小
   * @type {'small' | 'medium' | 'large'}
   */
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  }
})

// 计算属性
/**
 * 头像容器尺寸样式类
 */
const sizeClass = computed(() => {
  const sizeMap = {
    small: 'w-8 h-8',
    medium: 'w-10 h-10',
    large: 'w-12 h-12'
  }
  return sizeMap[props.size]
})

/**
 * 图标尺寸样式类
 */
const iconSizeClass = computed(() => {
  const iconSizeMap = {
    small: 'w-4 h-4',
    medium: 'w-5 h-5',
    large: 'w-6 h-6'
  }
  return iconSizeMap[props.size]
})
</script>

<style scoped>
/* 头像组件样式 */
.avatar-container {
  transition: all 0.2s ease-in-out;
}

.avatar-container:hover {
  transform: scale(1.05);
}
</style>