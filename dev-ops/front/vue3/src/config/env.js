/**
 * 环境配置管理模块
 * 功能：统一管理不同环境下的配置信息
 * 适配Vue CLI项目的环境变量读取方式
 */

/**
 * 环境类型枚举
 */
export const ENV_TYPES = {
  DEVELOPMENT: 'development',
  PRODUCTION: 'production',
  TEST: 'test'
}

/**
 * 获取当前环境类型
 * @returns {string} 当前环境类型
 */
export const getCurrentEnv = () => {
  // Vue CLI 项目主要使用 NODE_ENV
  const mode = process.env.NODE_ENV || 'development'
  
  switch (mode) {
    case 'production':
    case 'prod':
      return ENV_TYPES.PRODUCTION
    case 'development':
    case 'dev':
      return ENV_TYPES.DEVELOPMENT
    case 'test':
    case 'testing':
      return ENV_TYPES.TEST
    default:
      return ENV_TYPES.DEVELOPMENT
  }
}

/**
 * 获取环境变量值
 * @param {string} key 环境变量键名
 * @param {*} defaultValue 默认值
 * @returns {*} 环境变量值
 */
const getEnvVar = (key, defaultValue = '') => {
  // Vue CLI 项目使用 process.env
  if (typeof process !== 'undefined' && process.env) {
    return process.env[key] || defaultValue
  }
  return defaultValue
}

/**
 * 环境配置对象
 */
const envConfig = {
  [ENV_TYPES.DEVELOPMENT]: {
    // 开发环境配置
    API_BASE_URL: getEnvVar('VUE_APP_API_BASE_URL', 'http://localhost:8888'),
    API_TIMEOUT: parseInt(getEnvVar('VUE_APP_API_TIMEOUT', '30000')),
    DEBUG: getEnvVar('VUE_APP_DEBUG', 'true') === 'true'
  },
  [ENV_TYPES.PRODUCTION]: {
    // 生产环境配置
    API_BASE_URL: getEnvVar('VUE_APP_API_BASE_URL', 'https://autoAgent.jasonlat.com'),
    API_TIMEOUT: parseInt(getEnvVar('VUE_APP_API_TIMEOUT', '30000')),
    DEBUG: getEnvVar('VUE_APP_DEBUG', 'false') === 'true'
  }
}

/**
 * 获取当前环境的配置
 * @returns {Object} 当前环境配置对象
 */
export const getEnvConfig = () => {
  const currentEnv = getCurrentEnv()
  return envConfig[currentEnv] || envConfig[ENV_TYPES.DEVELOPMENT]
}

/**
 * 获取API基础URL
 * @returns {string} API基础URL
 */
export const getApiBaseUrl = () => {
  return getEnvConfig().API_BASE_URL
}

/**
 * 获取API超时时间
 * @returns {number} API超时时间（毫秒）
 */
export const getApiTimeout = () => {
  return getEnvConfig().API_TIMEOUT
}

/**
 * 是否为调试模式
 * @returns {boolean} 是否为调试模式
 */
export const isDebugMode = () => {
  return getEnvConfig().DEBUG
}

/**
 * 是否为生产环境
 * @returns {boolean} 是否为生产环境
 */
export const isProduction = () => {
  return getCurrentEnv() === ENV_TYPES.PRODUCTION
}

/**
 * 是否为开发环境
 * @returns {boolean} 是否为开发环境
 */
export const isDevelopment = () => {
  return getCurrentEnv() === ENV_TYPES.DEVELOPMENT
}

// 导出当前环境配置
export default getEnvConfig()