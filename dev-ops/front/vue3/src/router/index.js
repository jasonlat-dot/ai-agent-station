/**
 * Vue Router 路由配置
 * 功能：管理应用的路由导航，设置/chat为默认路由
 */

import { createRouter, createWebHistory } from 'vue-router'
import ChatInterface from '../components/ChatInterface.vue'
import NotFound from '../components/NotFound.vue'

/**
 * 路由配置数组
 * 定义应用中所有可访问的路由路径
 */
const routes = [
  {
    path: '/',
    redirect: '/chat'
  },
  {
    path: '/chat',
    name: 'Chat',
    component: ChatInterface,
    meta: {
      title: 'AI Auto Agent - 智能对话助手'
    }
  },
  {
    // 404页面处理
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: {
      title: '页面未找到 - 404'
    }
  }
]

/**
 * 创建路由实例
 * 使用HTML5 History模式，支持动态base路径
 */
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL || '/'),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 路由切换时的滚动行为
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

/**
 * 全局前置守卫
 * 在每次路由跳转前执行，用于设置页面标题等
 */
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta && to.meta.title) {
    document.title = to.meta.title
  }
  
  next()
})

/**
 * 全局后置钩子
 * 在每次路由跳转后执行
 */
router.afterEach((to, from) => {
  // 可以在这里添加页面访问统计等逻辑
  console.log(`路由跳转: ${from.path} -> ${to.path}`)
})

export default router