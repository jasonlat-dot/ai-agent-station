<template>
  <!-- 聊天界面主组件 -->
  <div class="full-screen-layout bg-gray-50 font-sans">
    <!-- 顶部导航栏 -->
    <TopNavigation />

    <div class="flex-1 flex overflow-hidden">
      <!-- 侧边栏 -->
      <Sidebar 
        :is-visible="sidebarVisible"
        :chat-history="chatHistory"
        :selected-chat-id="currentChatId"
        :is-conversation-active="isLoading || isConnected"
        @select-chat="handleSelectChat"
        @delete-chat="handleDeleteChat"
        @edit-chat="handleEditChat"
        @new-chat="handleNewChat"
        @clear-all-chats="handleClearAllChats"
        @import-chat="handleImportChat"
        @export-chat="handleExportChat"
      />

      <!-- 主内容区域 -->
      <div class="flex-1 flex flex-col overflow-hidden bg-gray-50">
        <!-- 双面板内容区域 -->
        <DualPanel 
          ref="dualPanelRef"
          :thinking-messages="thinkingMessages"
          :result-messages="resultMessages"
        />

        <!-- 底部输入区域 -->
        <InputArea 
          ref="inputAreaRef"
          :is-loading="isLoading"
          :session-id="sessionId"
          @send-message="handleSendMessage"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import TopNavigation from './TopNavigation.vue'
import Sidebar from './Sidebar.vue'
import DualPanel from './DualPanel.vue'
import InputArea from './InputArea.vue'
import chatService from '../services/chatService.js'
import notificationService from '../services/notificationService.js'
import agentService from '../services/agentService.js'

/**
 * 聊天界面主组件
 * 功能：整合所有子组件，管理聊天状态和消息流，提供完整的对话记录管理
 */

// 响应式数据
const sidebarVisible = ref(true)
const isLoading = ref(false)
const sessionId = ref('')
const currentChatId = ref(null)
const eventSource = ref(null)
const isConnected = ref(false)

// 消息数据
const thinkingMessages = ref([])
const resultMessages = ref([])
const chatHistory = ref([])

// 组件引用
const dualPanelRef = ref(null)
const inputAreaRef = ref(null)



/**
 * 创建新的对话记录
 * @param {Object} options - 创建选项
 * @returns {Object} 新的对话记录
 */
const createNewChatRecord = (options = {}) => {
  // 如果传入了ID，直接构建对话记录而不是调用createChatRecord（避免生成新ID）
  if (options.id) {
    const now = new Date()
    const chat = {
      id: options.id, // 使用传入的ID
      title: options.title || chatService.extractTitleFromMessages([...thinkingMessages.value, ...resultMessages.value]),
      lastMessage: '暂无消息',
      timestamp: now,
      thinkingMessages: [...thinkingMessages.value],
      resultMessages: [...resultMessages.value],
      messageCount: 0,
      metadata: {
        createdAt: now,
        updatedAt: now,
        version: '1.0'
      }
    }
    
    // 更新消息计数和最后消息
    chatService.updateChatMetadata(chat)
    return chat
  }
  
  // 没有传入ID时，使用原来的逻辑创建新对话
  return chatService.createChatRecord({
    title: options.title || chatService.extractTitleFromMessages([...thinkingMessages.value, ...resultMessages.value]),
    thinkingMessages: [...thinkingMessages.value],
    resultMessages: [...resultMessages.value],
    ...options
  })
}

/**
 * 添加用户消息到两个面板
 * @param {String} content - 消息内容
 */
const addUserMessage = (content) => {
  const userMessage = {
    id: Date.now() + '_user',
    type: 'user',
    content: content,
    timestamp: new Date()
  }
  
  // 添加到两个面板
  thinkingMessages.value.push(userMessage)
  resultMessages.value.push(userMessage)
}

/**
 * 添加AI消息
 * @param {Object} messageData - 消息数据
 */
const addAIMessage = (messageData) => {
  const { type, subType, step, content } = messageData
  
  if (!content || content.trim() === '') {
    return // 忽略空内容
  }

  const aiMessage = {
    id: Date.now() + '_ai_' + Math.random().toString(36).substr(2, 9),
    type: 'ai',
    content: content,
    stage: type,
    subType: subType,
    step: step,
    timestamp: new Date()
  }

  // 根据消息类型决定显示在哪个面板
  if (type === 'summary' || type === 'completed') {
    // 总结和完成消息显示在右侧结果面板
    resultMessages.value.push(aiMessage)
  } else {
    // 其他消息（分析、执行、监督等）显示在左侧思考面板
    thinkingMessages.value.push(aiMessage)
  }
}

/**
 * 处理发送消息
 * @param {Object} requestData - 请求数据
 */
const handleSendMessage = async (requestData) => {
  if (isConnected.value) {
    notificationService.warning('正在处理中，请稍候...')
    return
  }

  // 显示用户消息
  addUserMessage(requestData.message)
  
  // 立即保存当前对话到历史记录（用户发送消息时）
  // 这样用户可以立即在列表中看到对话记录
  saveCurrentChatToHistory()

  // 设置加载状态
  isLoading.value = true
  isConnected.value = true

  try {
    // 使用agentService发送消息
    await agentService.sendMessage(requestData, {
      /**
       * 接收到消息时的回调函数
       * @param {Object} messageData - 消息数据
       */
      onMessage: (messageData) => {
        addAIMessage(messageData)
      },
      
      /**
       * 发生错误时的回调函数
       * @param {Error} error - 错误对象
       */
      onError: (error) => {
        console.error('Agent服务错误:', error)
        isConnected.value = false
        isLoading.value = false
        addAIMessage({
          type: 'error',
          content: error.message || '连接中断，请重试',
          subType: null,
          step: null
        })
        
        // 即使出错也保存当前对话
        saveCurrentChatToHistory()
      },
      
      /**
       * 完成时的回调函数
       */
      onComplete: () => {
        console.log('消息流处理完成')
        isConnected.value = false
        isLoading.value = false
        
        // 添加完成标识
        addAIMessage({
          type: 'complete',
          content: '任务执行完成',
          subType: null,
          step: null
        })
        
        // 自动保存当前对话到历史记录
        saveCurrentChatToHistory()
      }
    })
  } catch (error) {
    console.error('发送消息失败:', error)
    isConnected.value = false
    isLoading.value = false
    addAIMessage({
      type: 'error',
      content: '请求失败: ' + error.message,
      subType: null,
      step: null
    })
    
    // 即使出错也保存当前对话
    saveCurrentChatToHistory()
  }
}

// 防止重复保存的时间戳记录
const lastSaveTime = ref(0)
const SAVE_DEBOUNCE_TIME = 1000 // 1秒内不重复保存

/**
 * 保存当前对话到历史记录
 */
const saveCurrentChatToHistory = () => {
  try {
    // 检查是否有内容需要保存
    const hasContent = thinkingMessages.value.length > 0 || resultMessages.value.length > 0
    
    if (!hasContent) {
      console.log('当前对话无内容，跳过保存')
      return
    }

    // 防抖检查：避免短时间内重复保存
    const now = Date.now()
    if (now - lastSaveTime.value < SAVE_DEBOUNCE_TIME) {
      console.log('保存操作过于频繁，跳过本次保存')
      return
    }
    lastSaveTime.value = now

    // 检查是否已经存在当前对话ID的记录
    const existingIndex = chatHistory.value.findIndex(chat => chat.id === currentChatId.value)
    
    // 创建对话记录
    const chatRecord = createNewChatRecord({
      id: currentChatId.value
    })
    
    if (existingIndex !== -1) {
      // 检查是否真的需要更新（内容是否有变化）
      const existingChat = chatHistory.value[existingIndex]
      const existingMessageCount = (existingChat.thinkingMessages?.length || 0) + (existingChat.resultMessages?.length || 0)
      const newMessageCount = (chatRecord.thinkingMessages?.length || 0) + (chatRecord.resultMessages?.length || 0)
      
      if (existingMessageCount === newMessageCount) {
        console.log('对话内容无变化，跳过更新')
        return
      }
      
      // 更新现有记录
      const mergedChat = chatService.mergeChatRecords(existingChat, chatRecord)
      chatHistory.value[existingIndex] = mergedChat
      console.log('已更新对话历史记录:', mergedChat.title)
    } else {
      // 添加新记录到历史记录顶部
      chatHistory.value.unshift(chatRecord)
      console.log('已保存新对话到历史记录:', chatRecord.title)
    }
    
    // 限制历史记录数量
    if (chatHistory.value.length > chatService.maxHistoryCount) {
      chatHistory.value = chatHistory.value.slice(0, chatService.maxHistoryCount)
      console.log('历史记录已达到上限，自动清理旧记录')
    }
  } catch (error) {
    console.error('保存对话到历史记录失败:', error)
  }
}

/**
 * 处理新建对话
 */
const handleNewChat = () => {
  // 如果正在加载中，阻止新建对话
  if (isLoading.value) {
    console.warn('正在处理中，无法新建对话')
    return
  }

  try {
    // 直接重置当前对话状态，创建全新的空白对话
    // 注意：这里不保存当前对话，因为用户明确要求新建对话
    resetCurrentChat()
    
    console.log('新建对话完成，会话ID:', sessionId.value)
    notificationService.success('新对话已创建')
  } catch (error) {
    console.error('新建对话失败:', error)
    notificationService.error('新建对话失败，请重试')
  }
}

/**
 * 重置当前对话状态
 */
const resetCurrentChat = () => {
  try {
    // 清空消息列表
    thinkingMessages.value = []
    resultMessages.value = []
    
    // 重置连接状态
    isConnected.value = false
    isLoading.value = false
    
    // 生成新的聊天ID，并将sessionId设置为与chatId一致
    currentChatId.value = chatService.generateId()
    sessionId.value = currentChatId.value
    
    console.log('对话状态已重置，新的聊天ID:', currentChatId.value)
    
    // 清空输入框并聚焦
    nextTick(() => {
      inputAreaRef.value?.clearInput()
      // 滚动面板到顶部
      dualPanelRef.value?.scrollBothToBottom()
    })
  } catch (error) {
    console.error('重置对话状态失败:', error)
  }
}

/**
 * 处理清空所有对话
 */
const handleClearAllChats = () => {
  // 如果正在加载中，阻止清空操作
  if (isLoading.value) {
    console.warn('正在处理中，无法清空对话')
    return
  }

  // 检查是否有对话历史
  if (chatHistory.value.length === 0) {
    console.log('没有对话历史需要清空')
    return
  }

  try {
    // 记录清空前的统计信息
    const totalChats = chatHistory.value.length
    const totalMessages = chatHistory.value.reduce((sum, chat) => {
      return sum + (chat.messageCount || 0)
    }, 0)
    
    console.log(`准备清空 ${totalChats} 个对话，共 ${totalMessages} 条消息`)
    
    // 清空对话历史
    chatHistory.value = []
    
    // 重置当前对话
    resetCurrentChat()
    
    // 清空本地存储
    chatService.saveChatHistory([])
    console.log('已清空本地存储的对话历史')
    
    console.log(`成功清空所有对话历史 (${totalChats} 个对话)`)
    notificationService.success(`已清空 ${totalChats} 个对话记录`)
    
    // 显示成功提示
    nextTick(() => {
      console.log('清空操作完成，界面已重置')
    })
  } catch (error) {
    console.error('清空对话历史失败:', error)
    notificationService.error('清空对话失败，请重试')
  }
}

/**
 * 处理选择对话
 * @param {Object} chat - 对话对象
 */
const handleSelectChat = (chat) => {
  // 如果正在加载中，阻止切换对话
  if (isLoading.value) {
    console.warn('正在处理中，无法切换对话')
    return
  }

  // 验证对话数据
  if (!chatService.validateChatRecord(chat)) {
    console.error('无效的对话记录')
    return
  }

  // 如果选择的是当前对话，不需要切换
  if (chat.id === currentChatId.value) {
    console.log('已经是当前对话，无需切换')
    return
  }

  try {
    // 保存当前对话（如果有内容且不是空对话）
    const hasCurrentContent = thinkingMessages.value.length > 0 || resultMessages.value.length > 0
    if (currentChatId.value && hasCurrentContent && currentChatId.value !== chat.id) {
      // 只有在切换到不同对话时才保存当前对话
      saveCurrentChatToHistory()
    }

    // 重置连接状态
    isConnected.value = false
    isLoading.value = false

    // 加载选中的对话
    currentChatId.value = chat.id
    thinkingMessages.value = [...(chat.thinkingMessages || [])]
    resultMessages.value = [...(chat.resultMessages || [])]
    
    // 更新会话ID（保持与对话ID一致）
    sessionId.value = chat.id
    
    console.log('已切换到对话:', chat.title || '未命名对话')
    
    // 滚动到底部并清空输入框
    nextTick(() => {
      dualPanelRef.value?.scrollBothToBottom()
      inputAreaRef.value?.clearInput()
    })
  } catch (error) {
    console.error('切换对话失败:', error)
    notificationService.error('切换对话失败，请重试')
  }
}

/**
 * 处理删除对话
 * @param {String} chatId - 对话ID
 */
const handleDeleteChat = (chatId) => {
  // 如果要删除的是当前正在进行的对话，阻止删除操作
  if (isLoading.value && chatId === currentChatId.value) {
    console.warn('当前对话正在进行中，无法删除')
    notificationService.warning('当前对话正在进行中，无法删除')
    return
  }

  try {
    const index = chatHistory.value.findIndex(chat => chat.id === chatId)
    if (index === -1) {
      console.warn('要删除的对话不存在')
      return
    }

    const deletedChat = chatHistory.value[index]
    console.log('删除对话:', deletedChat.title || '未命名对话')
    
    // 从历史记录中移除
    chatHistory.value.splice(index, 1)
    
    // 如果删除的是当前对话
    if (chatId === currentChatId.value) {
      // 尝试切换到下一个对话，如果没有则创建新对话
      if (chatHistory.value.length > 0) {
        // 选择下一个对话（如果删除的不是最后一个）或前一个对话
        const nextIndex = index < chatHistory.value.length ? index : index - 1
        const nextChat = chatHistory.value[nextIndex]
        if (nextChat) {
          handleSelectChat(nextChat)
          console.log('已切换到下一个对话:', nextChat.title)
          return
        }
      }
      
      // 没有其他对话，创建新对话
      resetCurrentChat()
      console.log('已创建新对话')
    }
    
    console.log(`对话删除完成，剩余 ${chatHistory.value.length} 个对话`)
    notificationService.success('对话已删除')
  } catch (error) {
    console.error('删除对话失败:', error)
    notificationService.error('删除对话失败，请重试')
  }
}

/**
 * 处理编辑对话
 * @param {String} chatId - 对话ID
 */
const handleEditChat = (chatId) => {
  try {
    const chat = chatHistory.value.find(c => c.id === chatId)
    if (!chat) {
      console.warn('要编辑的对话不存在')
      return
    }

    const newTitle = prompt('请输入新的对话标题:', chat.title || '新对话')
    if (newTitle !== null && newTitle.trim() !== '') {
      const trimmedTitle = newTitle.trim()
      
      // 更新对话标题
      chat.title = trimmedTitle
      
      // 更新元数据
      chatService.updateChatMetadata(chat)
      
      console.log('对话标题已更新:', trimmedTitle)
      notificationService.success('对话标题已更新')
      
      // 如果是当前对话，也需要更新显示
      if (chatId === currentChatId.value) {
        // 触发响应式更新
        chatHistory.value = [...chatHistory.value]
      }
      
      // 自动保存
      chatService.autoSaveChatHistory(chatHistory.value)
    }
  } catch (error) {
    console.error('编辑对话失败:', error)
    notificationService.error('编辑对话失败，请重试')
  }
}

/**
 * 导出对话历史
 * @param {String} format - 导出格式
 */
const exportChatHistory = (format = 'json') => {
  try {
    const data = chatService.exportChatHistory(chatHistory.value, format)
    const blob = new Blob([data], { type: format === 'json' ? 'application/json' : 'text/plain' })
    const url = URL.createObjectURL(blob)
    
    const link = document.createElement('a')
    link.href = url
    link.download = `chat_history_${new Date().toISOString().split('T')[0]}.${format}`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    
    console.log('对话历史导出成功')
    notificationService.success('对话历史导出成功')
  } catch (error) {
    console.error('导出对话历史失败:', error)
    notificationService.error('导出失败，请重试')
  }
}

/**
 * 处理导入对话（从侧边栏触发）
 * @param {File} file - 导入文件
 */
const handleImportChat = async (file) => {
  try {
    const text = await file.text()
    const importedChats = chatService.importChatHistory(text, 'json')
    
    if (importedChats.length === 0) {
      console.warn('导入文件中没有有效的对话记录')
      return
    }
    
    // 合并导入的对话到现有历史
    const mergedHistory = [...importedChats, ...chatHistory.value]
    const uniqueHistory = mergedHistory.filter((chat, index, self) => 
      index === self.findIndex(c => c.id === chat.id)
    )
    
    chatHistory.value = uniqueHistory.slice(0, chatService.maxHistoryCount)
    
    console.log(`成功导入 ${importedChats.length} 个对话记录`)
    notificationService.success(`成功导入 ${importedChats.length} 个对话记录`)
    
    // 显示导入成功提示
    nextTick(() => {
      console.log('对话历史导入完成，界面已更新')
    })
  } catch (error) {
    console.error('导入对话历史失败:', error)
    notificationService.error('导入失败，请检查文件格式')
  }
}

/**
 * 处理导出对话（从侧边栏触发）
 * @param {String} format - 导出格式
 */
const handleExportChat = (format = 'json') => {
  try {
    if (chatHistory.value.length === 0) {
      console.warn('没有对话历史可以导出')
      notificationService.warning('没有对话历史可以导出')
      return
    }
    
    exportChatHistory(format)
  } catch (error) {
    console.error('导出对话历史失败:', error)
  }
}

/**
 * 导入对话历史（通用方法）
 * @param {File} file - 导入文件
 */
const importChatHistory = async (file) => {
  return handleImportChat(file)
}

/**
 * 获取第一条用户消息作为对话标题
 * @returns {String} 对话标题
 */
const getFirstUserMessage = () => {
  const userMessage = thinkingMessages.value.find(msg => msg.type === 'user')
  return userMessage ? userMessage.content.substring(0, 30) + (userMessage.content.length > 30 ? '...' : '') : ''
}

/**
 * 获取最后一条AI消息作为预览
 * @returns {String} 最后一条AI消息
 */
const getLastAIMessage = () => {
  const aiMessages = [...thinkingMessages.value, ...resultMessages.value].filter(msg => msg.type === 'ai')
  const lastMessage = aiMessages[aiMessages.length - 1]
  return lastMessage ? lastMessage.content.substring(0, 50) + (lastMessage.content.length > 50 ? '...' : '') : ''
}

/**
 * 从本地存储加载对话历史
 */
const loadChatHistory = () => {
  try {
    chatHistory.value = chatService.loadChatHistory()
    console.log(`已加载 ${chatHistory.value.length} 个历史对话`)
  } catch (error) {
    console.error('加载对话历史失败:', error)
    chatHistory.value = []
  }
}

/**
 * 保存对话历史到本地存储
 */
const saveChatHistory = () => {
  try {
    const success = chatService.saveChatHistory(chatHistory.value)
    if (success) {
      console.log('对话历史已保存到本地存储')
    }
  } catch (error) {
    console.error('保存对话历史失败:', error)
  }
}

/**
 * 自动保存对话历史（防抖处理）
 */
const autoSaveChatHistory = () => {
  chatService.autoSaveChatHistory(chatHistory.value)
}

/**
 * 生成新的对话会话
 * 功能：初始化新的对话会话，重置相关状态
 */
const generateNewSession = () => {
  try {
    // 重置当前对话状态
    resetCurrentChat()
    
    // 生成新的会话ID
    currentChatId.value = chatService.generateId()
    
    // 重置输入状态
    isLoading.value = false
    
    console.log('新对话会话已生成:', currentChatId.value)
  } catch (error) {
    console.error('生成新对话会话失败:', error)
    notificationService.error('创建新对话失败')
  }
}

// 监听对话历史变化，自动保存
watch(
  () => chatHistory.value,
  () => {
    autoSaveChatHistory()
  },
  { deep: true }
)

// 组件挂载时初始化
onMounted(() => {
  try {
    // 加载历史对话
    loadChatHistory()
    
    // 初始化新对话
    generateNewSession()
    currentChatId.value = chatService.generateId()
    
    // 页面卸载前保存数据
    window.addEventListener('beforeunload', saveChatHistory)
    
    console.log('聊天界面初始化完成')
  } catch (error) {
    console.error('聊天界面初始化失败:', error)
    notificationService.error('应用初始化失败，请刷新页面')
  }
})

// 组件卸载时清理
onUnmounted(() => {
  try {
    // 保存数据
    saveChatHistory()
    
    // 清理对话服务资源
    chatService.cleanup()
    
    // 移除事件监听器
    window.removeEventListener('beforeunload', saveChatHistory)
    
    console.log('聊天界面资源清理完成')
  } catch (error) {
    console.error('聊天界面清理失败:', error)
  }
  
  // 清理通知服务
  notificationService.cleanup()
})

// 暴露方法给父组件或测试使用
defineExpose({
  exportChatHistory,
  importChatHistory,
  loadChatHistory,
  saveChatHistory,
  resetCurrentChat,
  chatService
})
</script>

<style scoped>
/* 全屏布局样式 */
.full-screen-layout {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 字体样式 */
.font-sans {
  font-family: Inter, sans-serif;
}

/* 背景色 */
.bg-gray-50 {
  background-color: #f9fafb;
}

/* 弹性布局 */
.flex {
  display: flex;
}

.flex-1 {
  flex: 1;
}

.flex-col {
  flex-direction: column;
}

/* 溢出处理 */
.overflow-hidden {
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .full-screen-layout {
    height: 100vh;
    height: 100dvh; /* 动态视口高度，适配移动设备 */
  }
}

/* 确保组件占满整个屏幕 */
:deep(.flex-1) {
  flex: 1;
}

:deep(.overflow-hidden) {
  overflow: hidden;
}
</style>