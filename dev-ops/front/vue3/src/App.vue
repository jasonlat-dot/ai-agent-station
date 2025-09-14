<script setup>
// Vue Router 会自动处理组件的加载和渲染
</script>

<template>
  <!-- AI Auto Agent 聊天界面应用 -->
  <div id="app">
    <!-- 路由视图容器，根据当前路由渲染对应组件 -->
    <router-view />
  </div>
</template>

<style>
/* 引入全局样式 */
@import './assets/styles/global.css';

/* 重置默认样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  overflow: hidden;
}

#app {
  height: 100vh;
  width: 100vw;
  font-family: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

/* 确保全屏布局 */
.full-screen-layout {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

/* 移动端适配 */
@media (max-width: 768px) {
  #app {
    height: 100vh;
    height: 100dvh; /* 动态视口高度 */
  }
}
</style>
