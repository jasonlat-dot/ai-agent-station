<template>
  <!-- 404页面组件 -->
  <div class="not-found-container">
    <!-- 背景动画 -->
    <div class="background-animation">
      <div class="floating-cloud cloud-1"></div>
      <div class="floating-cloud cloud-2"></div>
      <div class="floating-cloud cloud-3"></div>
      <div class="star star-1"></div>
      <div class="star star-2"></div>
      <div class="star star-3"></div>
      <div class="star star-4"></div>
      <div class="star star-5"></div>
    </div>

    <!-- 主要内容 -->
    <div class="content">
      <!-- 404标题 -->
      <div class="error-code">
        <span class="digit bounce-1">4</span>
        <span class="digit bounce-2">0</span>
        <span class="digit bounce-3">4</span>
      </div>

      <!-- 可爱的小动物 -->
      <div class="animal-container">
        <div class="cat" @click="playAnimation">
          <!-- 猫咪身体 -->
          <div class="cat-body">
            <!-- 猫咪头部 -->
            <div class="cat-head">
              <!-- 耳朵 -->
              <div class="ear ear-left"></div>
              <div class="ear ear-right"></div>
              <!-- 眼睛 -->
              <div class="eye eye-left">
                <div class="pupil"></div>
              </div>
              <div class="eye eye-right">
                <div class="pupil"></div>
              </div>
              <!-- 鼻子 -->
              <div class="nose"></div>
              <!-- 嘴巴 -->
              <div class="mouth"></div>
              <!-- 胡须 -->
              <div class="whisker whisker-left-1"></div>
              <div class="whisker whisker-left-2"></div>
              <div class="whisker whisker-right-1"></div>
              <div class="whisker whisker-right-2"></div>
            </div>
            <!-- 尾巴 -->
            <div class="tail"></div>
          </div>
        </div>
      </div>

      <!-- 错误信息 -->
      <div class="error-message">
        <h2 class="title">哎呀！页面走丢了</h2>
        <p class="description">
          看起来你访问的页面不存在，或者已经被移动到了其他地方。
          <br>
          不过别担心，我们的小猫咪会帮你找到回家的路！
        </p>
      </div>

      <!-- 操作按钮 -->
      <div class="actions">
        <button class="btn btn-primary" @click="goHome">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
            <polyline points="9,22 9,12 15,12 15,22"></polyline>
          </svg>
          回到首页
        </button>
        <button class="btn btn-secondary" @click="goBack">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M19 12H5M12 19l-7-7 7-7"></path>
          </svg>
          返回上页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

/**
 * 404页面组件
 * 功能：显示友好的404错误页面，包含动态效果和可爱的小动物
 */

const router = useRouter()
const isAnimating = ref(false)

/**
 * 返回首页
 */
const goHome = () => {
  router.push('/chat')
}

/**
 * 返回上一页
 */
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    goHome()
  }
}

/**
 * 播放小猫动画
 */
const playAnimation = () => {
  if (isAnimating.value) return
  
  isAnimating.value = true
  const cat = document.querySelector('.cat')
  cat.classList.add('playing')
  
  setTimeout(() => {
    cat.classList.remove('playing')
    isAnimating.value = false
  }, 2000)
}
</script>

<style scoped>
.not-found-container {
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* 背景动画 - 简约版 */
.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  animation: float 8s ease-in-out infinite;
}

.cloud-1 {
  width: 120px;
  height: 50px;
  top: 15%;
  left: 5%;
  animation-delay: 0s;
}

.cloud-2 {
  width: 100px;
  height: 40px;
  top: 25%;
  right: 10%;
  animation-delay: 3s;
}

.cloud-3 {
  width: 140px;
  height: 60px;
  bottom: 20%;
  left: 15%;
  animation-delay: 6s;
}

.star {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: twinkle 3s ease-in-out infinite;
}

.star-1 { top: 10%; left: 20%; animation-delay: 0s; }
.star-2 { top: 20%; right: 25%; animation-delay: 1s; }
.star-3 { bottom: 25%; left: 10%; animation-delay: 2s; }
.star-4 { top: 35%; left: 70%; animation-delay: 1.5s; }
.star-5 { bottom: 15%; right: 15%; animation-delay: 2.5s; }

/* 主要内容 */
.content {
  text-align: center;
  z-index: 1;
  max-width: 600px;
  padding: 2rem;
}

/* 404数字 */
.error-code {
  font-size: 8rem;
  font-weight: 900;
  color: #4a5568;
  margin-bottom: 2rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.digit {
  display: inline-block;
  animation: bounce 2s ease-in-out infinite;
}

.bounce-1 { animation-delay: 0s; }
.bounce-2 { animation-delay: 0.2s; }
.bounce-3 { animation-delay: 0.4s; }

/* 小猫咪 */
.animal-container {
  margin: 2rem 0;
  display: flex;
  justify-content: center;
}

.cat {
  position: relative;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.cat:hover {
  transform: scale(1.1);
}

.cat-body {
  width: 80px;
  height: 60px;
  background: #ff9500;
  border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;
  position: relative;
  animation: breathe 3s ease-in-out infinite;
}

.cat-head {
  width: 60px;
  height: 50px;
  background: #ff9500;
  border-radius: 50%;
  position: absolute;
  top: -25px;
  left: 10px;
}

.ear {
  width: 15px;
  height: 20px;
  background: #ff9500;
  border-radius: 50% 50% 0 0;
  position: absolute;
  top: 5px;
}

.ear-left {
  left: 10px;
  transform: rotate(-20deg);
}

.ear-right {
  right: 10px;
  transform: rotate(20deg);
}

.eye {
  width: 8px;
  height: 8px;
  background: #333;
  border-radius: 50%;
  position: absolute;
  top: 20px;
}

.eye-left { left: 15px; }
.eye-right { right: 15px; }

.pupil {
  width: 4px;
  height: 4px;
  background: #fff;
  border-radius: 50%;
  position: absolute;
  top: 1px;
  left: 1px;
  animation: blink 4s ease-in-out infinite;
}

.nose {
  width: 4px;
  height: 3px;
  background: #ff6b6b;
  border-radius: 50%;
  position: absolute;
  top: 28px;
  left: 28px;
}

.mouth {
  width: 12px;
  height: 6px;
  border: 2px solid #333;
  border-top: none;
  border-radius: 0 0 50% 50%;
  position: absolute;
  top: 32px;
  left: 22px;
}

.whisker {
  width: 15px;
  height: 1px;
  background: #333;
  position: absolute;
  top: 30px;
}

.whisker-left-1 { left: 5px; transform: rotate(10deg); }
.whisker-left-2 { left: 5px; top: 35px; transform: rotate(-10deg); }
.whisker-right-1 { right: 5px; transform: rotate(-10deg); }
.whisker-right-2 { right: 5px; top: 35px; transform: rotate(10deg); }

.tail {
  width: 30px;
  height: 8px;
  background: #ff9500;
  border-radius: 50%;
  position: absolute;
  right: -25px;
  top: 20px;
  transform: rotate(45deg);
  animation: wag 2s ease-in-out infinite;
}

/* 错误信息 */
.error-message {
  margin: 2rem 0;
  color: #4a5568;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.description {
  font-size: 1.1rem;
  line-height: 1.6;
  opacity: 0.9;
}

/* 操作按钮 */
.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 2rem;
}

.btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn-primary {
  background: #4299e1;
  color: white;
  border: 1px solid #4299e1;
}

.btn-primary:hover {
  background: #3182ce;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
}

.btn-secondary {
  background: transparent;
  color: #4a5568;
  border: 2px solid #cbd5e0;
}

.btn-secondary:hover {
  background: #f7fafc;
  color: #2d3748;
  border-color: #a0aec0;
  transform: translateY(-2px);
}

.icon {
  width: 18px;
  height: 18px;
  stroke-width: 2;
}

/* 动画效果 */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes twinkle {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

@keyframes breathe {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes blink {
  0%, 90%, 100% { opacity: 1; }
  95% { opacity: 0; }
}

@keyframes wag {
  0%, 100% { transform: rotate(45deg); }
  50% { transform: rotate(25deg); }
}

/* 点击动画 */
.cat.playing {
  animation: jump 0.6s ease-in-out;
}

.cat.playing .tail {
  animation: excited-wag 0.3s ease-in-out infinite;
}

@keyframes jump {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.1); }
}

@keyframes excited-wag {
  0%, 100% { transform: rotate(45deg); }
  25% { transform: rotate(15deg); }
  75% { transform: rotate(75deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: 1rem;
  }
  
  .error-code {
    font-size: 6rem;
  }
  
  .title {
    font-size: 1.5rem;
  }
  
  .description {
    font-size: 1rem;
  }
  
  .actions {
    flex-direction: column;
    align-items: center;
  }
  
  .btn {
    width: 200px;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .error-code {
    font-size: 4rem;
  }
  
  .cat-body {
    width: 60px;
    height: 45px;
  }
  
  .cat-head {
    width: 45px;
    height: 38px;
    top: -20px;
    left: 7px;
  }
}
</style>