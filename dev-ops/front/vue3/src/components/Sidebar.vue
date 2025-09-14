<template>
  <!-- 侧边栏组件 -->
  <aside 
    :class="[
      'w-64 bg-white border-r overflow-hidden transition-all duration-300 ease-in-out shadow-sm flex flex-col',
      { 'hidden': !isVisible }
    ]"
  >
    <!-- 头部区域 -->
    <div class="p-4 border-b border-gray-100 bg-gradient-to-r from-primary-50 to-blue-50">
      <!-- 标题 -->
      <h2 class="font-bold mb-3 text-lg text-gray-800 flex items-center gap-2">
        <div class="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
        </div>
        <span>对话历史</span>
        <span class="ml-auto text-xs bg-primary-100 text-primary-700 px-2 py-1 rounded-full">
          {{ filteredChatHistory.length }}
        </span>
      </h2>
      
      <!-- 搜索框 -->
      <div class="relative">
        <input 
          ref="searchInput"
          v-model="searchQuery"
          type="text" 
          placeholder="搜索对话..."
          class="w-full px-3 py-2 pr-10 text-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-300 focus:border-blue-500 outline-none transition-all duration-200 bg-white"
          @keyup.enter="handleSearch"
        >
        <el-tooltip v-if="searchQuery" content="清空搜索" placement="top" :show-after="500">
          <button 
            @click="clearSearch"
            class="absolute right-1 top-1/2 transform -translate-y-1/2 p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 transition-all duration-200 rounded-md border border-transparent hover:border-red-200"
          >
            <el-icon :size="14">
              <Close />
            </el-icon>
          </button>
        </el-tooltip>
      </div>
    </div>

    <!-- 对话列表区域 -->
    <div class="flex-1 overflow-y-auto custom-scrollbar">
      <!-- 分组显示 -->
      <div v-for="group in groupedChatHistory" :key="group.label" class="mb-4">
        <!-- 分组标题 -->
        <div class="sticky top-0 bg-gray-50 px-4 py-2 text-xs font-semibold text-gray-600 uppercase tracking-wide border-b border-gray-100">
          {{ group.label }}
          <span class="ml-2 text-primary-600">({{ group.chats.length }})</span>
        </div>
        
        <!-- 对话列表 -->
        <div class="px-2 py-2">
          <div 
            v-for="chat in group.chats" 
            :key="chat.id"
            @click="selectChat(chat)"
            :class="[
              'group relative p-4 mx-2 mb-2 rounded-2xl cursor-pointer transition-all duration-300 border-2 transform',
              {
                'bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 border-blue-300 shadow-lg scale-105 ring-2 ring-blue-200 ring-opacity-50': chat.id === selectedChatId,
                'bg-white border-gray-100 hover:border-blue-200 hover:shadow-md hover:scale-102': chat.id !== selectedChatId
              }
            ]"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1 min-w-0">
                <!-- 对话图标和标题 -->
                <div class="flex items-center gap-2 mb-2">
                  <div :class="[
                    'w-6 h-6 rounded-full flex items-center justify-center text-xs font-semibold',
                    chat.id === selectedChatId ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-600'
                  ]">
                    {{ getInitial(chat.title) }}
                  </div>
                  <h3 class="text-sm font-medium text-gray-800 truncate flex-1">
                    {{ chat.title || '新对话' }}
                  </h3>
                </div>
                
                <!-- 最后一条消息预览 -->
                <p class="text-xs text-gray-500 mb-2 line-clamp-2 leading-relaxed">
                  {{ chat.lastMessage || '暂无消息' }}
                </p>
                
                <!-- 底部信息 -->
                <div class="flex items-center justify-between text-xs">
                  <span class="text-gray-400 flex items-center gap-1">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    {{ formatTime(chat.timestamp) }}
                  </span>
                  <div class="flex items-center gap-2">
                    <!-- 对话大小 -->
                    <span class="bg-gray-50 text-gray-600 px-2 py-1 rounded-md text-xs whitespace-nowrap border border-gray-200" title="对话大小">
                      {{ getChatSize(chat) }}
                    </span>
                    <!-- 消息数量 -->
                    <span class="bg-gray-100 text-gray-600 px-2 py-1 rounded-full text-xs whitespace-nowrap" title="消息数量">
                      {{ getMessageCount(chat) }}条
                    </span>
                  </div>
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <div class="flex items-center gap-1 ml-2 opacity-0 group-hover:opacity-100 transition-all duration-300">
                <!-- 编辑按钮 -->
                <el-tooltip content="编辑对话" placement="top" :show-after="500">
                  <button 
                    @click.stop="editChat(chat.id)"
                    class="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 transition-all duration-200 rounded-md border border-transparent hover:border-blue-200"
                  >
                    <el-icon :size="14">
                      <Edit />
                    </el-icon>
                  </button>
                </el-tooltip>
                <!-- 删除按钮 -->
                <el-tooltip content="删除对话" placement="top" :show-after="500">
                  <button 
                    @click.stop="deleteChat(chat.id)"
                    class="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 transition-all duration-200 rounded-md border border-transparent hover:border-red-200"
                  >
                    <el-icon :size="14">
                      <Delete />
                    </el-icon>
                  </button>
                </el-tooltip>
              </div>
            </div>
            

          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="filteredChatHistory.length === 0" class="text-center py-12 px-4">
        <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-3.582 8-8 8a8.013 8.013 0 01-7-4L5 20l.94-3.542A8.003 8.003 0 0112 4c4.418 0 8 3.582 8 8z" />
          </svg>
        </div>
        <p class="text-sm text-gray-500 font-medium mb-1">
          {{ searchQuery ? '未找到匹配的对话' : '暂无对话历史' }}
        </p>
        <p class="text-xs text-gray-400">
          {{ searchQuery ? '尝试使用其他关键词搜索' : '开始新对话来创建历史记录' }}
        </p>
      </div>
    </div>

    <!-- 底部操作区域 -->
    <div class="p-4 border-t border-gray-100 bg-gray-50">
      <div class="flex flex-col gap-2">
        <!-- 新建对话按钮 -->
        <button 
          @click="handleNewChat"
          class="flex items-center justify-center gap-2 w-full px-4 py-3 text-gray-700 border border-gray-300 rounded-lg hover:bg-blue-50 hover:border-blue-400 hover:text-blue-700 hover:shadow-md hover:scale-105 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-300 font-medium transform"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 transition-transform duration-300 hover:rotate-90" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
          </svg>
          新建对话
        </button>
        
        <!-- 管理按钮组 -->
        <div class="flex gap-2">
          <!-- 导入按钮 -->
          <button 
            @click="handleImport"
            class="flex items-center justify-center gap-1 flex-1 px-3 py-2 text-xs text-blue-600 bg-white border border-blue-200 rounded-lg hover:bg-blue-50 hover:border-blue-300 hover:shadow-md hover:scale-105 hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-300 transform"
            title="导入对话历史"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 transition-transform duration-300 hover:scale-110" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10"/>
            </svg>
            导入
          </button>
          
          <!-- 导出按钮 -->
          <button 
            @click="handleExport"
            :disabled="chatHistory.length === 0"
            class="flex items-center justify-center gap-1 flex-1 px-3 py-2 text-xs text-green-600 bg-white border border-green-200 rounded-lg hover:bg-green-50 hover:border-green-300 hover:shadow-md hover:scale-105 hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-all duration-300 transform disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white disabled:hover:shadow-none disabled:hover:scale-100 disabled:hover:translate-y-0"
            title="导出对话历史"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 transition-transform duration-300 hover:scale-110" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"/>
            </svg>
            导出
          </button>
        </div>
        
        <!-- 清空对话按钮 -->
        <button 
          @click="handleClearAllChats"
          :disabled="chatHistory.length === 0"
          class="flex items-center justify-center gap-2 w-full px-4 py-2 text-sm text-red-600 bg-white border border-red-200 rounded-lg hover:bg-red-50 hover:border-red-300 hover:text-red-700 hover:shadow-md hover:scale-105 hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-all duration-300 transform disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white disabled:hover:shadow-none disabled:hover:scale-100 disabled:hover:translate-y-0"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 transition-transform duration-300 hover:scale-110 hover:rotate-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
          </svg>
          清空对话
        </button>
      </div>
    </div>
  </aside>


</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Edit, Delete, Search, Close } from '@element-plus/icons-vue'
import Avatar from './Avatar.vue'

/**
 * 侧边栏组件
 * 功能：显示对话历史列表，支持搜索、分组、选择和删除对话
 */

// Props
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: true
  },
  chatHistory: {
    type: Array,
    default: () => []
  },
  selectedChatId: {
    type: String,
    default: null
  },
  isConversationActive: {
    type: Boolean,
    default: false
  }
})

// 事件
const emit = defineEmits(['select-chat', 'delete-chat', 'edit-chat', 'new-chat', 'clear-all-chats', 'import-chat', 'export-chat'])

// 响应式数据
const searchQuery = ref('')
const searchInput = ref(null)

/**
 * 过滤后的对话历史
 */
const filteredChatHistory = computed(() => {
  if (!searchQuery.value.trim()) {
    return props.chatHistory
  }
  
  const query = searchQuery.value.toLowerCase()
  return props.chatHistory.filter(chat => {
    return (
      (chat.title && chat.title.toLowerCase().includes(query)) ||
      (chat.lastMessage && chat.lastMessage.toLowerCase().includes(query))
    )
  })
})

/**
 * 分组后的对话历史
 */
const groupedChatHistory = computed(() => {
  const groups = {
    today: { label: '今天', chats: [] },
    yesterday: { label: '昨天', chats: [] },
    thisWeek: { label: '本周', chats: [] },
    thisMonth: { label: '本月', chats: [] },
    older: { label: '更早', chats: [] }
  }
  
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000)
  const thisWeekStart = new Date(today.getTime() - (today.getDay() * 24 * 60 * 60 * 1000))
  const thisMonthStart = new Date(now.getFullYear(), now.getMonth(), 1)
  
  filteredChatHistory.value.forEach(chat => {
    const chatDate = new Date(chat.timestamp)
    const chatDay = new Date(chatDate.getFullYear(), chatDate.getMonth(), chatDate.getDate())
    
    if (chatDay.getTime() === today.getTime()) {
      groups.today.chats.push(chat)
    } else if (chatDay.getTime() === yesterday.getTime()) {
      groups.yesterday.chats.push(chat)
    } else if (chatDate >= thisWeekStart) {
      groups.thisWeek.chats.push(chat)
    } else if (chatDate >= thisMonthStart) {
      groups.thisMonth.chats.push(chat)
    } else {
      groups.older.chats.push(chat)
    }
  })
  
  // 只返回有内容的分组
  return Object.values(groups).filter(group => group.chats.length > 0)
})

/**
 * 选择对话
 * @param {Object} chat - 对话对象
 */
const selectChat = (chat) => {
  // 检查当前对话是否正在进行中
  if (props.isConversationActive) {
    ElMessage({
      message: '当前对话正在进行中，请等待对话结束后再切换',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }
  
  emit('select-chat', chat)
}

/**
 * 删除对话
 * @param {String} chatId - 对话ID
 */
const deleteChat = async (chatId) => {
  // 检查是否要删除当前正在进行的对话
  if (props.isConversationActive && chatId === props.selectedChatId) {
    ElMessage({
      message: '当前对话正在进行中，无法删除',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }
  
  try {
    await ElMessageBox.confirm(
      '确定要删除这个对话吗？删除后无法恢复。',
      '删除对话',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    emit('delete-chat', chatId)
    ElMessage.success('对话已删除')
  } catch {
    // 用户取消删除
  }
}

/**
 * 编辑对话
 * @param {String} chatId - 对话ID
 */
const editChat = (chatId) => {
  emit('edit-chat', chatId)
}

/**
 * 处理新建对话
 */
const handleNewChat = () => {
  // 检查当前对话是否正在进行中
  if (props.isConversationActive) {
    ElMessage({
      message: '当前对话正在进行中，请等待对话结束后再新建对话',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
    return
  }
  
  emit('new-chat')
}

/**
 * 处理清空所有对话
 */
const handleClearAllChats = async () => {
  if (props.chatHistory.length === 0) return
  
  try {
    await ElMessageBox.confirm(
      `确定要清空所有 ${props.chatHistory.length} 个对话吗？此操作无法撤销。`,
      '清空所有对话',
      {
        confirmButtonText: '确认清空',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    emit('clear-all-chats')
    ElMessage.success(`已清空 ${props.chatHistory.length} 个对话记录`)
  } catch {
    // 用户取消清空
  }
}



/**
 * 处理导入对话
 */
const handleImport = () => {
  // 创建文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  input.onchange = (event) => {
    const file = event.target.files[0]
    if (file) {
      emit('import-chat', file)
    }
  }
  input.click()
}

/**
 * 处理导出对话
 */
const handleExport = () => {
  emit('export-chat', 'json')
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  if (searchInput.value) {
    searchInput.value.focus()
  }
  // 搜索功能已通过computed属性filteredChatHistory实现
  // 这里可以添加额外的搜索逻辑，如搜索历史记录等
}

/**
 * 清空搜索
 */
const clearSearch = () => {
  searchQuery.value = ''
  if (searchInput.value) {
    searchInput.value.focus()
  }
}

/**
 * 获取对话标题首字母
 * @param {String} title - 对话标题
 * @returns {String} 首字母
 */
const getInitial = (title) => {
  if (!title) return 'N'
  const firstChar = title.charAt(0)
  return /[\u4e00-\u9fa5]/.test(firstChar) ? firstChar : firstChar.toUpperCase()
}

/**
 * 获取消息数量
 * @param {Object} chat - 对话对象
 * @returns {Number} 消息数量
 */
const getMessageCount = (chat) => {
  const thinkingCount = chat.thinkingMessages ? chat.thinkingMessages.length : 0
  const resultCount = chat.resultMessages ? chat.resultMessages.length : 0
  return thinkingCount + resultCount
}

/**
 * 计算对话大小
 * @param {Object} chat - 对话对象
 * @returns {String} 格式化后的大小字符串
 */
const getChatSize = (chat) => {
  if (!chat) return '0B'
  
  // 计算对话对象的JSON字符串大小
  const chatString = JSON.stringify(chat)
  const sizeInBytes = new Blob([chatString]).size
  
  // 格式化大小显示
  if (sizeInBytes < 1024) {
    return `${sizeInBytes}B`
  } else if (sizeInBytes < 1024 * 1024) {
    return `${(sizeInBytes / 1024).toFixed(1)}KB`
  } else if (sizeInBytes < 1024 * 1024 * 1024) {
    return `${(sizeInBytes / (1024 * 1024)).toFixed(1)}MB`
  } else {
    return `${(sizeInBytes / (1024 * 1024 * 1024)).toFixed(1)}GB`
  }
}

/**
 * 格式化时间
 * @param {Date|String} timestamp - 时间戳
 * @returns {String} 格式化后的时间字符串
 */
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  
  // 小于1小时，显示具体分钟
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    if (minutes === 1) {
      return '1分钟前'
    } else if (minutes < 30) {
      return `${minutes}分钟前`
    } else {
      return '半小时前'
    }
  }
  
  // 小于1天，显示具体小时
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    if (hours === 1) {
      return '1小时前'
    } else if (hours < 12) {
      return `${hours}小时前`
    } else {
      return '半天前'
    }
  }
  
  // 小于7天，显示具体天数
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000)
    if (days === 1) {
      return '1天前'
    } else {
      return `${days}天前`
    }
  }
  
  // 小于30天，显示周数
  if (diff < 2592000000) {
    const weeks = Math.floor(diff / 604800000)
    if (weeks === 1) {
      return '1周前'
    } else {
      return `${weeks}周前`
    }
  }
  
  // 超过30天显示具体日期
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
/* 滚动条样式 */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 2px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
}

/* 多行文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  aside {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 40;
    transform: translateX(-100%);
    transition: transform 0.3s ease-in-out;
  }
  
  aside.visible {
    transform: translateX(0);
  }
}

/* 悬停效果 */
.hover\:border-primary-200:hover {
  border-color: #bfdbfe;
}

.hover\:shadow-sm:hover {
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.hover\:text-blue-500:hover {
  color: #3b82f6;
}

.hover\:text-red-500:hover {
  color: #ef4444;
}

.hover\:text-gray-600:hover {
  color: #4b5563;
}

/* 过渡效果 */
.transition-all {
  transition: all 0.2s ease;
}

.transition-colors {
  transition-property: color, background-color, border-color;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

.transition-opacity {
  transition-property: opacity;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

/* 文本截断 */
.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 最小宽度 */
.min-w-0 {
  min-width: 0;
}

/* 渐变背景 */
.bg-gradient-to-r {
  background-image: linear-gradient(to right, var(--tw-gradient-stops));
}

.from-primary-50 {
  --tw-gradient-from: #eff6ff;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(239, 246, 255, 0));
}

.to-blue-50 {
  --tw-gradient-to: #eff6ff;
}

/* 组件特定样式 */
.group:hover .opacity-0 {
  opacity: 1;
}

/* 焦点样式 */
.focus\:ring-2:focus {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
}

.focus\:ring-primary-300:focus {
  box-shadow: 0 0 0 2px rgba(147, 197, 253, 0.5);
}

.focus\:border-primary-500:focus {
  border-color: #3b82f6;
}

/* 颜色样式 */
.text-primary-600 {
  color: #2563eb;
}

.text-primary-700 {
  color: #1d4ed8;
}

.bg-primary-50 {
  background-color: #eff6ff;
}

.bg-primary-100 {
  background-color: #dbeafe;
}

.bg-primary-600 {
  background-color: #2563eb;
}

.border-primary-200 {
  border-color: #bfdbfe;
}

.border-gray-100 {
  border-color: #f3f4f6;
}

.border-gray-200 {
  border-color: #e5e7eb;
}

/* 渐变背景样式 */
.bg-gradient-to-r {
  background-image: linear-gradient(to right, var(--tw-gradient-stops));
}

.bg-gradient-to-br {
  background-image: linear-gradient(to bottom right, var(--tw-gradient-stops));
}

.from-blue-500 {
  --tw-gradient-from: #3b82f6;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(59, 130, 246, 0));
}

.to-purple-600 {
  --tw-gradient-to: #9333ea;
}

.from-blue-600 {
  --tw-gradient-from: #2563eb;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(37, 99, 235, 0));
}

.to-purple-700 {
  --tw-gradient-to: #7c3aed;
}

.from-red-100 {
  --tw-gradient-from: #fee2e2;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(254, 226, 226, 0));
}

.to-red-200 {
  --tw-gradient-to: #fecaca;
}

.from-red-500 {
  --tw-gradient-from: #ef4444;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(239, 68, 68, 0));
}

.to-red-600 {
  --tw-gradient-to: #dc2626;
}

.from-red-600 {
  --tw-gradient-from: #dc2626;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(220, 38, 38, 0));
}

.to-red-700 {
  --tw-gradient-to: #b91c1c;
}

/* 阴影效果 */
.shadow-lg {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.shadow-xl {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.shadow-2xl {
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.hover\:shadow-md:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.hover\:shadow-xl:hover {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* 变换效果 */
.transform {
  transform: translateX(var(--tw-translate-x)) translateY(var(--tw-translate-y)) rotate(var(--tw-rotate)) skewX(var(--tw-skew-x)) skewY(var(--tw-skew-y)) scaleX(var(--tw-scale-x)) scaleY(var(--tw-scale-y));
}

.hover\:scale-105:hover {
  transform: scale(1.05);
}

.hover\:scale-110:hover {
  transform: scale(1.1);
}

.scale-100 {
  transform: scale(1);
}

/* 圆角样式 */
.rounded-xl {
  border-radius: 0.75rem;
}

.rounded-2xl {
  border-radius: 1rem;
}

.rounded-full {
  border-radius: 9999px;
}

/* 背景模糊 */
.backdrop-blur-sm {
  backdrop-filter: blur(4px);
}

/* 动画效果 */
@keyframes fade-in {
  0% {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.animate-fade-in {
  animation: fade-in 0.3s ease-out;
}

/* 行高 */
.leading-relaxed {
  line-height: 1.625;
}

/* Element Plus 按钮样式优化 */
.p-1\.5 {
  padding: 0.375rem;
}

.rounded-md {
  border-radius: 0.375rem;
}

.border-transparent {
  border-color: transparent;
}

.hover\:border-blue-200:hover {
  border-color: #bfdbfe;
}

.hover\:border-red-200:hover {
  border-color: #fecaca;
}

.hover\:bg-blue-50:hover {
  background-color: #eff6ff;
}

.hover\:bg-red-50:hover {
  background-color: #fef2f2;
}

.hover\:text-blue-600:hover {
  color: #2563eb;
}

.hover\:text-red-600:hover {
  color: #dc2626;
}

.text-gray-500 {
  color: #6b7280;
}

/* 新增样式 */
.bg-gradient-to-br {
  background-image: linear-gradient(to bottom right, var(--tw-gradient-stops));
}

.from-blue-50 {
  --tw-gradient-from: #eff6ff;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(239, 246, 255, 0));
}

.via-indigo-50 {
  --tw-gradient-via: #eef2ff;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-via), var(--tw-gradient-to, rgba(238, 242, 255, 0));
}

.to-purple-50 {
  --tw-gradient-to: #faf5ff;
}

.border-blue-300 {
  border-color: #93c5fd;
}

.hover\:border-blue-200:hover {
  border-color: #bfdbfe;
}

.hover\:scale-102:hover {
  transform: scale(1.02);
}

.scale-105 {
  transform: scale(1.05);
}

.ring-2 {
  box-shadow: 0 0 0 2px var(--tw-ring-color);
}

.ring-blue-200 {
  --tw-ring-color: #bfdbfe;
}

.ring-opacity-50 {
  --tw-ring-opacity: 0.5;
  --tw-ring-color: rgba(191, 219, 254, var(--tw-ring-opacity));
}

.rounded-2xl {
  border-radius: 1rem;
}

.hover\:shadow-md:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 确认对话框样式 */
.fixed {
  position: fixed;
}

.inset-0 {
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

.bg-black {
  background-color: #000000;
}

.bg-opacity-50 {
  background-color: rgba(0, 0, 0, 0.5);
}

.z-50 {
  z-index: 50;
}

.max-w-md {
  max-width: 28rem;
}

.mx-4 {
  margin-left: 1rem;
  margin-right: 1rem;
}

.shadow-xl {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.transform {
  transform: translateX(var(--tw-translate-x)) translateY(var(--tw-translate-y)) rotate(var(--tw-rotate)) skewX(var(--tw-skew-x)) skewY(var(--tw-skew-y)) scaleX(var(--tw-scale-x)) scaleY(var(--tw-scale-y));
}

.p-6 {
  padding: 1.5rem;
}

.mb-4 {
  margin-bottom: 1rem;
}

.mb-6 {
  margin-bottom: 1.5rem;
}

.w-10 {
  width: 2.5rem;
}

.h-10 {
  height: 2.5rem;
}

.bg-red-100 {
  background-color: #fee2e2;
}

.text-red-600 {
  color: #dc2626;
}

.h-5 {
  height: 1.25rem;
}

.w-5 {
  width: 1.25rem;
}

.text-lg {
  font-size: 1.125rem;
}

.font-semibold {
  font-weight: 600;
}

.text-gray-900 {
  color: #111827;
}

.text-gray-600 {
  color: #4b5563;
}

.justify-end {
  justify-content: flex-end;
}

.bg-gray-100 {
  background-color: #f3f4f6;
}

.bg-red-600 {
  background-color: #dc2626;
}

.bg-red-700 {
  background-color: #b91c1c;
}

/* 禁用状态样式 */
.disabled\:opacity-50:disabled {
  opacity: 0.5;
}

.disabled\:cursor-not-allowed:disabled {
  cursor: not-allowed;
}

.disabled\:hover\:bg-white:disabled:hover {
  background-color: #ffffff;
}

/* 悬停效果 */
.hover\:bg-primary-700:hover {
  background-color: #1d4ed8;
}

.hover\:bg-red-50:hover {
  background-color: #fef2f2;
}

.hover\:bg-gray-200:hover {
  background-color: #e5e7eb;
}

.hover\:bg-red-700:hover {
  background-color: #b91c1c;
}

/* 焦点样式 */
.focus\:ring-offset-2:focus {
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px rgba(59, 130, 246, 0.5);
}

.focus\:ring-red-500:focus {
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.5);
}

.focus\:ring-gray-500:focus {
  box-shadow: 0 0 0 2px rgba(107, 114, 128, 0.5);
}
</style>