import api from './api.js'
import { isDebugMode, getApiBaseUrl } from '../config/env.js'

/**
 * Agent服务模块
 * 功能：封装与AI代理相关的API调用，包括流式响应处理
 */

/**
 * Agent服务类
 * 提供与AI代理交互的各种方法
 */
class AgentService {
  /**
   * 构造函数
   * 初始化服务实例
   */
  constructor() {
    this.baseEndpoint = '/agent'
  }

  /**
   * 发送消息给AI代理（流式响应）
   * @param {Object} requestData - 请求数据
   * @param {string} requestData.message - 用户消息
   * @param {Array} requestData.history - 对话历史
   * @param {Function} onMessage - 接收到消息时的回调函数
   * @param {Function} onError - 发生错误时的回调函数
   * @param {Function} onComplete - 完成时的回调函数
   * @returns {Promise<Object>} 包含取消方法的对象
   */
  async sendMessage(requestData, { onMessage, onError, onComplete } = {}) {
    try {
      if (isDebugMode()) {
        console.log('发送消息到AI代理:', requestData)
      }
      
      // 使用fetch进行流式请求（axios对SSE支持有限）
        const response = await fetch(`${getApiBaseUrl()}/api/v1/agent/auto_agent`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'text/event-stream'
          },
          body: JSON.stringify(requestData)
        })

      if (!response.ok) {
        throw new Error(`网络请求失败: ${response.status}`)
      }

      // 处理流式响应
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let cancelled = false

      /**
       * 取消请求的方法
       */
      const cancel = () => {
        cancelled = true
        reader.cancel()
      }

      /**
       * 读取流数据的递归函数
       */
      const readStream = async () => {
        try {
          const { done, value } = await reader.read()
          
          if (cancelled) {
            return
          }
          
          if (done) {
            // 流结束
            if (isDebugMode()) {
              console.log('消息流结束')
            }
            if (onComplete) {
              onComplete()
            }
            return
          }

          // 解码数据块
          const chunk = decoder.decode(value, { stream: true })

          // 处理服务器发送事件流数据
          const lines = chunk.split('\n')
          for (let line of lines) {
            if (line.startsWith('data: ')) {
              const data = line.substring(6).trim()
              if (data && data !== '[DONE]') {
                try {
                  // 尝试解析JSON数据
                  const jsonData = JSON.parse(data)
                  if (onMessage) {
                    onMessage(jsonData)
                  }
                } catch (e) {
                  // 如果不是JSON，作为纯文本处理
                  if (isDebugMode()) {
                    console.warn('无法解析JSON数据:', data)
                  }
                  if (onMessage) {
                    onMessage({
                      type: 'error',
                      content: data,
                      subType: null,
                      step: null
                    })
                  }
                }
              }
            }
          }
          
          // 继续读取流
          await readStream()
        } catch (error) {
          if (!cancelled) {
            if (isDebugMode()) {
              console.error('读取流数据错误:', error)
            }
            if (onError) {
              onError(error)
            }
          }
        }
      }

      // 开始读取流
      await readStream()

      return { cancel }
    } catch (error) {
      if (isDebugMode()) {
        console.error('请求错误:', error)
      }
      if (onError) {
        onError(error)
      }
      throw error
    }
  }

  /**
   * 获取代理状态
   * @returns {Promise<Object>} 代理状态信息
   */
  async getAgentStatus() {
    try {
      if (isDebugMode()) {
        console.log('获取代理状态')
      }
      const response = await api.get(`${this.baseEndpoint}/status`)
      return response.data
    } catch (error) {
      if (isDebugMode()) {
        console.error('获取代理状态失败:', error)
      }
      throw error
    }
  }

  /**
   * 停止当前任务
   * @returns {Promise<Object>} 停止结果
   */
  async stopCurrentTask() {
    try {
      if (isDebugMode()) {
        console.log('停止当前任务')
      }
      const response = await api.post(`${this.baseEndpoint}/stop`)
      return response.data
    } catch (error) {
      if (isDebugMode()) {
        console.error('停止任务失败:', error)
      }
      throw error
    }
  }

  /**
   * 获取代理配置
   * @returns {Promise<Object>} 代理配置信息
   */
  async getAgentConfig() {
    try {
      const response = await api.get(`${this.baseEndpoint}/config`)
      return response.data
    } catch (error) {
      console.error('获取代理配置失败:', error)
      throw error
    }
  }

  /**
   * 更新代理配置
   * @param {Object} config - 新的配置
   * @returns {Promise<Object>} 更新结果
   */
  async updateAgentConfig(config) {
    try {
      const response = await api.put(`${this.baseEndpoint}/config`, config)
      return response.data
    } catch (error) {
      console.error('更新代理配置失败:', error)
      throw error
    }
  }

  /**
   * 获取对话历史
   * @param {number} limit - 限制数量
   * @param {number} offset - 偏移量
   * @returns {Promise<Object>} 对话历史数据
   */
  async getChatHistory(limit = 50, offset = 0) {
    try {
      const response = await api.get(`${this.baseEndpoint}/history`, {
        params: { limit, offset }
      })
      return response.data
    } catch (error) {
      console.error('获取对话历史失败:', error)
      throw error
    }
  }

  /**
   * 删除对话历史
   * @param {string} chatId - 对话ID
   * @returns {Promise<Object>} 删除结果
   */
  async deleteChatHistory(chatId) {
    try {
      const response = await api.delete(`${this.baseEndpoint}/history/${chatId}`)
      return response.data
    } catch (error) {
      console.error('删除对话历史失败:', error)
      throw error
    }
  }
}

/**
 * 创建并导出agent服务实例
 */
const agentService = new AgentService()

export { AgentService }
export default agentService