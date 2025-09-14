<template>
  <!-- 消息项组件 -->
  <div class="flex items-start gap-3 message">
    <!-- 头像 -->
    <Avatar 
      :name="message.type === 'user' ? 'User' : 'AI'"
      :type="message.type === 'user' ? 'user' : 'ai'"
      :size="32"
    />

    <!-- 消息内容 -->
    <div 
      :class="[
        'flex-1 rounded-lg p-3',
        message.type === 'user' ? 'bg-green-50' : 'bg-primary-50'
      ]"
    >
      <div 
        :class="[
          'text-sm',
          message.type === 'user' ? 'text-green-800' : 'text-primary-800'
        ]"
      >
        <!-- 发送者标识 -->
        <strong>{{ message.type === 'user' ? '您:' : 'AI助手:' }}</strong>
        
        <!-- AI消息的阶段指示器 -->
        <template v-if="message.type === 'ai' && (message.stage || message.subType || message.step)">
          <span 
            v-if="message.stage"
            :class="['stage-indicator', getStageClass(message.stage)]"
          >
            {{ getStageIcon(message.stage) }} {{ getStageName(message.stage) }}
          </span>
          <span 
            v-if="message.subType"
            class="sub-type-indicator"
          >
            {{ getSubTypeName(message.subType) }}
          </span>
          <span 
            v-if="message.step"
            class="sub-type-indicator"
          >
            第{{ message.step }}步
          </span>
        </template>
        
        <!-- 消息内容 -->
        <div 
          v-if="message.type === 'user'"
          class="mt-1"
        >
          {{ message.content }}
        </div>
        <div 
          v-else
          class="markdown-content"
          v-html="renderedContent"
        >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, nextTick } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import Avatar from './Avatar.vue'

/**
 * 消息项组件
 * 功能：显示单条消息，支持用户消息和AI消息的不同样式
 */

// Props
const props = defineProps({
  message: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value === 'object' && value.content
    }
  }
})

// 阶段类型映射
const stageTypeMap = {
  'analysis': { name: '分析阶段', icon: '🎯', class: 'stage-analysis' },
  'execution': { name: '执行阶段', icon: '⚡', class: 'stage-execution' },
  'supervision': { name: '监督阶段', icon: '🔍', class: 'stage-supervision' },
  'summary': { name: '总结阶段', icon: '📊', class: 'stage-summary' },
  'error': { name: '错误信息', icon: '❌', class: 'stage-error' },
  'complete': { name: '完成', icon: '✅', class: 'stage-summary' }
}

// 子类型映射
const subTypeMap = {
  'analysis_status': '任务状态',
  'analysis_history': '历史评估',
  'analysis_strategy': '执行策略',
  'analysis_progress': '完成度',
  'analysis_task_status': '任务状态',
  'execution_target': '执行目标',
  'execution_process': '执行过程',
  'execution_result': '执行结果',
  'execution_quality': '质量检查',
  'assessment': '质量评估',
  'issues': '问题识别',
  'suggestions': '改进建议',
  'score': '质量评分',
  'pass': '检查结果',
  'completed_work': '已完成工作',
  'incomplete_reasons': '未完成原因',
  'evaluation': '效果评估',
  'summary_overview': '总结概览'
}

/**
 * 渲染Markdown内容
 */
const renderedContent = computed(() => {
  if (props.message.type === 'user') {
    return props.message.content
  }
  
  try {
    // 配置marked选项
    marked.setOptions({
      highlight: function(code, lang) {
        if (lang && hljs.getLanguage(lang)) {
          try {
            return hljs.highlight(code, { language: lang }).value
          } catch (err) {
            console.warn('代码高亮失败:', err)
          }
        }
        return hljs.highlightAuto(code).value
      },
      breaks: true,
      gfm: true
    })
    
    // 解析Markdown并清理HTML
    const html = marked.parse(props.message.content || '')
    return DOMPurify.sanitize(html)
  } catch (error) {
    console.error('Markdown渲染失败:', error)
    return props.message.content || ''
  }
})

/**
 * 获取阶段样式类
 * @param {String} stage - 阶段类型
 * @returns {String} CSS类名
 */
const getStageClass = (stage) => {
  return stageTypeMap[stage]?.class || 'stage-analysis'
}

/**
 * 获取阶段图标
 * @param {String} stage - 阶段类型
 * @returns {String} 图标
 */
const getStageIcon = (stage) => {
  return stageTypeMap[stage]?.icon || '📝'
}

/**
 * 获取阶段名称
 * @param {String} stage - 阶段类型
 * @returns {String} 阶段名称
 */
const getStageName = (stage) => {
  return stageTypeMap[stage]?.name || stage
}

/**
 * 获取子类型名称
 * @param {String} subType - 子类型
 * @returns {String} 子类型名称
 */
const getSubTypeName = (subType) => {
  return subTypeMap[subType] || subType
}

/**
 * 组件挂载后高亮代码块
 */
onMounted(() => {
  nextTick(() => {
    // 高亮所有代码块
    const codeBlocks = document.querySelectorAll('pre code')
    codeBlocks.forEach((block) => {
      hljs.highlightElement(block)
    })
  })
})
</script>

<style scoped>
/* 消息动画 */
@keyframes messageSlideIn {
  from { 
    opacity: 0; 
    transform: translateY(10px); 
  }
  to { 
    opacity: 1; 
    transform: translateY(0); 
  }
}

.message {
  animation: messageSlideIn 0.3s ease-out;
}

/* 阶段指示器样式 */
.stage-indicator {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  margin-right: 8px;
  text-transform: uppercase;
}

.stage-analysis {
  background-color: #e3f2fd;
  color: #1976d2;
}

.stage-execution {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.stage-supervision {
  background-color: #fff3e0;
  color: #f57c00;
}

.stage-summary {
  background-color: #e8f5e8;
  color: #388e3c;
}

.stage-error {
  background-color: #ffebee;
  color: #d32f2f;
}

.sub-type-indicator {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 500;
  margin-left: 4px;
  background-color: rgba(0, 0, 0, 0.1);
  color: rgba(0, 0, 0, 0.7);
}

/* Markdown内容样式 */
.markdown-content {
  margin-top: 8px;
  line-height: 1.6;
}

.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4),
.markdown-content :deep(h5),
.markdown-content :deep(h6) {
  margin: 16px 0 8px 0;
  font-weight: 600;
  color: #2c3e50;
}

.markdown-content :deep(h1) { font-size: 1.5em; }
.markdown-content :deep(h2) { font-size: 1.3em; }
.markdown-content :deep(h3) { font-size: 1.2em; }
.markdown-content :deep(h4) { font-size: 1.1em; }

.markdown-content :deep(p) {
  margin: 8px 0;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin: 8px 0;
  padding-left: 20px;
}

.markdown-content :deep(li) {
  margin: 4px 0;
}

.markdown-content :deep(blockquote) {
  border-left: 4px solid #3498db;
  margin: 16px 0;
  padding: 8px 16px;
  background: #f8f9fa;
  color: #555;
  font-style: italic;
}

.markdown-content :deep(code) {
  background: #f1f3f4;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
  color: #d73a49;
}

.markdown-content :deep(pre) {
  background: #f6f8fa;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  padding: 16px;
  overflow-x: auto;
  margin: 16px 0;
}

.markdown-content :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
  font-size: 0.85em;
}

.markdown-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 16px 0;
}

.markdown-content :deep(th),
.markdown-content :deep(td) {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}

.markdown-content :deep(th) {
  background: #f5f5f5;
  font-weight: 600;
}

.markdown-content :deep(a) {
  color: #3498db;
  text-decoration: none;
}

.markdown-content :deep(a:hover) {
  text-decoration: underline;
}

.markdown-content :deep(strong) {
  font-weight: 600;
  color: #2c3e50;
}

.markdown-content :deep(em) {
  font-style: italic;
  color: #555;
}
</style>