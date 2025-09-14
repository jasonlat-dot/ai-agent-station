/**
 * 对话管理服务
 * 功能：提供对话的完整生命周期管理，包括创建、保存、加载、编辑、删除等
 */

/**
 * 对话数据结构接口
 * @typedef {Object} ChatMessage
 * @property {string} id - 消息ID
 * @property {string} type - 消息类型 ('user' | 'ai')
 * @property {string} content - 消息内容
 * @property {string} [stage] - AI消息阶段
 * @property {string} [subType] - AI消息子类型
 * @property {number} [step] - 执行步骤
 * @property {Date} timestamp - 时间戳
 */

/**
 * @typedef {Object} ChatRecord
 * @property {string} id - 对话ID
 * @property {string} title - 对话标题
 * @property {string} lastMessage - 最后一条消息预览
 * @property {Date} timestamp - 创建/更新时间
 * @property {ChatMessage[]} thinkingMessages - 思考过程消息
 * @property {ChatMessage[]} resultMessages - 结果消息
 * @property {number} messageCount - 消息总数
 * @property {Object} [metadata] - 元数据
 */

class ChatService {
  constructor() {
    this.storageKey = 'ai_agent_chat_history'
    this.maxHistoryCount = 100
    this.autoSaveDelay = 1000
    this.saveTimeout = null
  }

  /**
   * 生成唯一ID
   * @returns {string} 唯一标识符
   */
  generateId() {
    return `chat_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }

  /**
   * 生成会话ID
   * @returns {string} 会话标识符
   */
  generateSessionId() {
    return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }

  /**
   * 验证对话数据
   * @param {ChatRecord} chat - 对话记录
   * @returns {boolean} 是否有效
   */
  validateChatRecord(chat) {
    if (!chat || typeof chat !== 'object') {
      console.warn('对话记录必须是对象')
      return false
    }

    if (!chat.id || typeof chat.id !== 'string') {
      console.warn('对话记录必须有有效的ID')
      return false
    }

    if (!Array.isArray(chat.thinkingMessages)) {
      console.warn('思考消息必须是数组')
      return false
    }

    if (!Array.isArray(chat.resultMessages)) {
      console.warn('结果消息必须是数组')
      return false
    }

    return true
  }

  /**
   * 清理和标准化对话数据
   * @param {ChatRecord} chat - 原始对话数据
   * @returns {ChatRecord} 清理后的对话数据
   */
  sanitizeChatRecord(chat) {
    return {
      id: chat.id || this.generateId(),
      title: chat.title || '新对话',
      lastMessage: chat.lastMessage || '暂无消息',
      timestamp: chat.timestamp ? new Date(chat.timestamp) : new Date(),
      thinkingMessages: Array.isArray(chat.thinkingMessages) ? chat.thinkingMessages : [],
      resultMessages: Array.isArray(chat.resultMessages) ? chat.resultMessages : [],
      messageCount: chat.messageCount || 0,
      metadata: chat.metadata || {}
    }
  }

  /**
   * 从本地存储加载对话历史
   * @returns {ChatRecord[]} 对话历史列表
   */
  loadChatHistory() {
    try {
      const savedData = localStorage.getItem(this.storageKey)
      if (!savedData) {
        console.log('没有找到保存的对话历史')
        return []
      }

      const parsedData = JSON.parse(savedData)
      if (!Array.isArray(parsedData)) {
        console.warn('保存的对话历史格式无效')
        return []
      }

      const validChats = parsedData
        .map(chat => this.sanitizeChatRecord(chat))
        .filter(chat => this.validateChatRecord(chat))
        .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))

      console.log(`成功加载 ${validChats.length} 个对话记录`)
      return validChats

    } catch (error) {
      console.error('加载对话历史失败:', error)
      return []
    }
  }

  /**
   * 保存对话历史到本地存储
   * @param {ChatRecord[]} chatHistory - 对话历史列表
   * @returns {boolean} 是否保存成功
   */
  saveChatHistory(chatHistory) {
    try {
      if (!Array.isArray(chatHistory)) {
        console.warn('对话历史必须是数组')
        return false
      }

      // 验证和清理数据
      const validChats = chatHistory
        .filter(chat => this.validateChatRecord(chat))
        .slice(0, this.maxHistoryCount) // 限制数量

      const dataToSave = JSON.stringify(validChats)
      localStorage.setItem(this.storageKey, dataToSave)
      
      console.log(`成功保存 ${validChats.length} 个对话记录`)
      return true

    } catch (error) {
      console.error('保存对话历史失败:', error)
      return false
    }
  }

  /**
   * 自动保存对话历史（防抖处理）
   * @param {ChatRecord[]} chatHistory - 对话历史列表
   */
  autoSaveChatHistory(chatHistory) {
    if (this.saveTimeout) {
      clearTimeout(this.saveTimeout)
    }

    this.saveTimeout = setTimeout(() => {
      this.saveChatHistory(chatHistory)
    }, this.autoSaveDelay)
  }

  /**
   * 创建新对话记录
   * @param {Object} options - 创建选项
   * @param {string} [options.title] - 对话标题
   * @param {ChatMessage[]} [options.thinkingMessages] - 思考消息
   * @param {ChatMessage[]} [options.resultMessages] - 结果消息
   * @returns {ChatRecord} 新的对话记录
   */
  createChatRecord(options = {}) {
    const now = new Date()
    const chat = {
      id: this.generateId(),
      title: options.title || `对话 ${now.toLocaleString('zh-CN', { 
        month: 'short', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
      })}`,
      lastMessage: '暂无消息',
      timestamp: now,
      thinkingMessages: options.thinkingMessages || [],
      resultMessages: options.resultMessages || [],
      messageCount: 0,
      metadata: {
        createdAt: now,
        updatedAt: now,
        version: '1.0'
      }
    }

    // 更新消息计数和最后消息
    this.updateChatMetadata(chat)
    
    console.log('创建新对话记录:', chat.title)
    return chat
  }

  /**
   * 更新对话元数据
   * @param {ChatRecord} chat - 对话记录
   */
  updateChatMetadata(chat) {
    if (!chat) return

    // 更新消息计数
    chat.messageCount = (chat.thinkingMessages?.length || 0) + (chat.resultMessages?.length || 0)

    // 更新最后一条消息
    const allMessages = [...(chat.thinkingMessages || []), ...(chat.resultMessages || [])]
    const lastMessage = allMessages
      .filter(msg => msg.content && msg.content.trim())
      .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))[0]

    if (lastMessage) {
      chat.lastMessage = lastMessage.content.length > 50 
        ? lastMessage.content.substring(0, 50) + '...' 
        : lastMessage.content
    }

    // 更新时间戳
    chat.timestamp = new Date()
    if (chat.metadata) {
      chat.metadata.updatedAt = new Date()
    }
  }

  /**
   * 从消息中提取对话标题
   * @param {ChatMessage[]} messages - 消息列表
   * @returns {string} 对话标题
   */
  extractTitleFromMessages(messages) {
    if (!Array.isArray(messages) || messages.length === 0) {
      return '新对话'
    }

    // 查找第一条用户消息
    const firstUserMessage = messages.find(msg => msg.type === 'user' && msg.content)
    if (firstUserMessage) {
      const content = firstUserMessage.content.trim()
      return content.length > 30 ? content.substring(0, 30) + '...' : content
    }

    return '新对话'
  }

  /**
   * 合并对话记录
   * @param {ChatRecord} existingChat - 现有对话
   * @param {ChatRecord} newChat - 新对话数据
   * @returns {ChatRecord} 合并后的对话
   */
  mergeChatRecords(existingChat, newChat) {
    if (!existingChat || !newChat) {
      return newChat || existingChat
    }

    const merged = {
      ...existingChat,
      ...newChat,
      thinkingMessages: [...(newChat.thinkingMessages || existingChat.thinkingMessages || [])],
      resultMessages: [...(newChat.resultMessages || existingChat.resultMessages || [])],
      metadata: {
        ...existingChat.metadata,
        ...newChat.metadata,
        updatedAt: new Date()
      }
    }

    this.updateChatMetadata(merged)
    return merged
  }

  /**
   * 清理过期的对话记录
   * @param {ChatRecord[]} chatHistory - 对话历史
   * @param {number} [maxAge] - 最大保存天数，默认30天
   * @returns {ChatRecord[]} 清理后的对话历史
   */
  cleanupExpiredChats(chatHistory, maxAge = 30) {
    if (!Array.isArray(chatHistory)) {
      return []
    }

    const cutoffDate = new Date()
    cutoffDate.setDate(cutoffDate.getDate() - maxAge)

    const validChats = chatHistory.filter(chat => {
      const chatDate = new Date(chat.timestamp)
      return chatDate > cutoffDate
    })

    const removedCount = chatHistory.length - validChats.length
    if (removedCount > 0) {
      console.log(`清理了 ${removedCount} 个过期对话记录`)
    }

    return validChats
  }

  /**
   * 导出对话历史
   * @param {ChatRecord[]} chatHistory - 对话历史
   * @param {string} [format] - 导出格式 ('json' | 'txt')
   * @returns {string} 导出的数据
   */
  exportChatHistory(chatHistory, format = 'json') {
    if (!Array.isArray(chatHistory)) {
      throw new Error('对话历史必须是数组')
    }

    if (format === 'json') {
      return JSON.stringify(chatHistory, null, 2)
    }

    if (format === 'txt') {
      return chatHistory.map(chat => {
        const header = `=== ${chat.title} (${chat.timestamp}) ===\n`
        const thinking = chat.thinkingMessages.map(msg => `[思考] ${msg.content}`).join('\n')
        const result = chat.resultMessages.map(msg => `[结果] ${msg.content}`).join('\n')
        return header + thinking + '\n' + result
      }).join('\n\n')
    }

    throw new Error(`不支持的导出格式: ${format}`)
  }

  /**
   * 导入对话历史
   * @param {string} data - 导入的数据
   * @param {string} [format] - 数据格式 ('json')
   * @returns {ChatRecord[]} 导入的对话历史
   */
  importChatHistory(data, format = 'json') {
    if (typeof data !== 'string') {
      throw new Error('导入数据必须是字符串')
    }

    if (format === 'json') {
      try {
        const parsed = JSON.parse(data)
        if (!Array.isArray(parsed)) {
          throw new Error('导入的数据必须是数组格式')
        }

        return parsed
          .map(chat => this.sanitizeChatRecord(chat))
          .filter(chat => this.validateChatRecord(chat))
      } catch (error) {
        throw new Error(`JSON解析失败: ${error.message}`)
      }
    }

    throw new Error(`不支持的导入格式: ${format}`)
  }

  /**
   * 清理资源
   */
  cleanup() {
    if (this.saveTimeout) {
      clearTimeout(this.saveTimeout)
      this.saveTimeout = null
    }
  }
}

// 创建单例实例
const chatService = new ChatService()

export default chatService

// 导出类供测试使用
export { ChatService }