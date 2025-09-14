/**
 * Vue CLI 配置文件
 * 功能：配置Vue CLI构建选项和开发服务器
 */
const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  // 禁用ESLint检查
  lintOnSave: false,
  
  // 公共路径配置
  publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
  
  // 页面配置
  pages: {
    index: {
      entry: 'src/main.js',
      filename: 'index.html',
      title: 'AI Auto Agent - 智能对话助手',
      chunks: ['chunk-vendors', 'chunk-common', 'index']
    }
  },
  
  // 输出目录
  outputDir: 'dist',
  
  // 静态资源目录
  assetsDir: 'static',
  
  // 生产环境配置
  productionSourceMap: process.env.NODE_ENV !== 'production'
})
