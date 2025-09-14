import axios from 'axios'
import { getApiBaseUrl, getApiTimeout, isDebugMode } from '../config/env.js'

/**
 * API模块
 * 功能：封装HTTP请求，提供统一的请求接口和错误处理
 */

/**
 * 创建axios实例
 * 配置基础URL、超时时间和默认请求头
 */
const apiClient = axios.create({
  baseURL: getApiBaseUrl(),
  timeout: getApiTimeout(),
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 请求拦截器
 * 功能：在请求发送前进行统一处理
 */
apiClient.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证token等
    if (isDebugMode()) {
      console.log('发送请求:', config.method?.toUpperCase(), config.url)
    }
    return config
  },
  (error) => {
    if (isDebugMode()) {
      console.error('请求拦截器错误:', error)
    }
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 功能：统一处理响应数据和错误
 */
apiClient.interceptors.response.use(
  (response) => {
    if (isDebugMode()) {
      console.log('收到响应:', response.status, response.config.url)
    }
    return response
  },
  (error) => {
    if (isDebugMode()) {
      console.error('响应拦截器错误:', error)
    }
    
    // 统一错误处理
    let errorMessage = '请求失败'
    
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response
      switch (status) {
        case 400:
          errorMessage = '请求参数错误'
          break
        case 401:
          errorMessage = '未授权访问'
          break
        case 403:
          errorMessage = '禁止访问'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = data?.message || `请求失败 (${status})`
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      errorMessage = '网络连接失败，请检查网络设置'
    } else {
      // 其他错误
      errorMessage = error.message || '未知错误'
    }
    
    // 创建统一的错误对象
    const apiError = new Error(errorMessage)
    apiError.originalError = error
    apiError.status = error.response?.status
    
    return Promise.reject(apiError)
  }
)

/**
 * API请求方法集合
 */
const api = {
  /**
   * GET请求
   * @param {string} url - 请求URL
   * @param {Object} config - axios配置选项
   * @returns {Promise} axios响应Promise
   */
  get: (url, config = {}) => {
    return apiClient.get(url, config)
  },

  /**
   * POST请求
   * @param {string} url - 请求URL
   * @param {Object} data - 请求数据
   * @param {Object} config - axios配置选项
   * @returns {Promise} axios响应Promise
   */
  post: (url, data = {}, config = {}) => {
    return apiClient.post(url, data, config)
  },

  /**
   * PUT请求
   * @param {string} url - 请求URL
   * @param {Object} data - 请求数据
   * @param {Object} config - axios配置选项
   * @returns {Promise} axios响应Promise
   */
  put: (url, data = {}, config = {}) => {
    return apiClient.put(url, data, config)
  },

  /**
   * DELETE请求
   * @param {string} url - 请求URL
   * @param {Object} config - axios配置选项
   * @returns {Promise} axios响应Promise
   */
  delete: (url, config = {}) => {
    return apiClient.delete(url, config)
  },

  /**
   * 流式请求（用于SSE）
   * @param {string} url - 请求URL
   * @param {Object} data - 请求数据
   * @param {Object} config - axios配置选项
   * @returns {Promise} axios响应Promise
   */
  stream: (url, data = {}, config = {}) => {
    const streamConfig = {
      ...config,
      headers: {
        ...config.headers,
        'Accept': 'text/event-stream',
        'Cache-Control': 'no-cache'
      },
      responseType: 'stream'
    }
    return apiClient.post(url, data, streamConfig)
  }
}

/**
 * 导出API实例和axios客户端
 */
export { api, apiClient }
export default api