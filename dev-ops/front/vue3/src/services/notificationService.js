/**
 * 通知管理服务
 * 功能：统一管理应用中的通知显示，支持多种类型的通知
 */

import { createApp } from 'vue'
import Toast from '../components/Toast.vue'

class NotificationService {
  constructor() {
    this.toasts = new Map()
    this.maxToasts = 5
    this.defaultDuration = 3000
  }

  /**
   * 显示通知
   * @param {Object} options - 通知选项
   * @param {string} options.type - 通知类型 ('success' | 'error' | 'warning' | 'info')
   * @param {string} options.title - 通知标题
   * @param {string} options.message - 通知消息
   * @param {number} [options.duration] - 显示时长（毫秒）
   * @param {boolean} [options.autoClose] - 是否自动关闭
   * @returns {string} 通知ID
   */
  show(options) {
    const {
      type = 'info',
      title = '',
      message = '',
      duration = this.defaultDuration,
      autoClose = true
    } = options

    if (!message) {
      console.warn('通知消息不能为空')
      return null
    }

    // 生成唯一ID
    const id = `toast_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

    // 如果通知数量超过限制，移除最旧的通知
    if (this.toasts.size >= this.maxToasts) {
      const oldestId = this.toasts.keys().next().value
      this.remove(oldestId)
    }

    // 创建容器元素
    const container = document.createElement('div')
    document.body.appendChild(container)

    // 创建Vue应用实例
    const app = createApp(Toast, {
      type,
      title,
      message,
      duration,
      autoClose,
      onClose: () => {
        this.remove(id)
      }
    })

    // 挂载组件
    const instance = app.mount(container)

    // 保存通知信息
    this.toasts.set(id, {
      app,
      instance,
      container,
      options: { type, title, message, duration, autoClose }
    })

    console.log(`显示${type}通知:`, message)
    return id
  }

  /**
   * 移除通知
   * @param {string} id - 通知ID
   */
  remove(id) {
    const toast = this.toasts.get(id)
    if (!toast) {
      return
    }

    try {
      // 卸载Vue应用
      toast.app.unmount()
      
      // 移除DOM元素
      if (toast.container && toast.container.parentNode) {
        toast.container.parentNode.removeChild(toast.container)
      }
      
      // 从映射中删除
      this.toasts.delete(id)
      
      console.log('通知已移除:', id)
    } catch (error) {
      console.error('移除通知失败:', error)
    }
  }

  /**
   * 清除所有通知
   */
  clear() {
    const ids = Array.from(this.toasts.keys())
    ids.forEach(id => this.remove(id))
    console.log('已清除所有通知')
  }

  /**
   * 显示成功通知
   * @param {string} message - 消息内容
   * @param {string} [title] - 标题
   * @param {Object} [options] - 其他选项
   * @returns {string} 通知ID
   */
  success(message, title = '成功', options = {}) {
    return this.show({
      type: 'success',
      title,
      message,
      ...options
    })
  }

  /**
   * 显示错误通知
   * @param {string} message - 消息内容
   * @param {string} [title] - 标题
   * @param {Object} [options] - 其他选项
   * @returns {string} 通知ID
   */
  error(message, title = '错误', options = {}) {
    return this.show({
      type: 'error',
      title,
      message,
      duration: 5000, // 错误通知显示更长时间
      ...options
    })
  }

  /**
   * 显示警告通知
   * @param {string} message - 消息内容
   * @param {string} [title] - 标题
   * @param {Object} [options] - 其他选项
   * @returns {string} 通知ID
   */
  warning(message, title = '警告', options = {}) {
    return this.show({
      type: 'warning',
      title,
      message,
      duration: 4000,
      ...options
    })
  }

  /**
   * 显示信息通知
   * @param {string} message - 消息内容
   * @param {string} [title] - 标题
   * @param {Object} [options] - 其他选项
   * @returns {string} 通知ID
   */
  info(message, title = '提示', options = {}) {
    return this.show({
      type: 'info',
      title,
      message,
      ...options
    })
  }

  /**
   * 获取当前通知数量
   * @returns {number} 通知数量
   */
  getCount() {
    return this.toasts.size
  }

  /**
   * 检查是否有指定类型的通知
   * @param {string} type - 通知类型
   * @returns {boolean} 是否存在
   */
  hasType(type) {
    for (const toast of this.toasts.values()) {
      if (toast.options.type === type) {
        return true
      }
    }
    return false
  }

  /**
   * 设置默认配置
   * @param {Object} config - 配置选项
   * @param {number} [config.maxToasts] - 最大通知数量
   * @param {number} [config.defaultDuration] - 默认显示时长
   */
  setConfig(config) {
    if (config.maxToasts !== undefined) {
      this.maxToasts = Math.max(1, Math.min(10, config.maxToasts))
    }
    if (config.defaultDuration !== undefined) {
      this.defaultDuration = Math.max(1000, config.defaultDuration)
    }
    console.log('通知服务配置已更新:', { 
      maxToasts: this.maxToasts, 
      defaultDuration: this.defaultDuration 
    })
  }

  /**
   * 清理资源
   */
  cleanup() {
    this.clear()
    console.log('通知服务资源已清理')
  }
}

// 创建单例实例
const notificationService = new NotificationService()

// 全局错误处理
window.addEventListener('error', (event) => {
  console.error('全局错误:', event.error)
  notificationService.error('应用发生错误，请刷新页面重试')
})

// 全局未处理的Promise拒绝
window.addEventListener('unhandledrejection', (event) => {
  console.error('未处理的Promise拒绝:', event.reason)
  notificationService.error('操作失败，请重试')
})

export default notificationService

// 导出类供测试使用
export { NotificationService }