/**
 * 选择框配置文件
 * 存储底部三个选择框的数据选项
 */

/**
 * 智能体类型选项
 */
export const agentOptions = [
  {
    value: '3',
    label: 'Auto Agent - 自动智能对话体'
  }
]

/**
 * 最大执行步数选项
 */
export const maxStepsOptions = [
  {
    value: '1',
    label: '1步'
  },
  {
    value: '2',
    label: '2步'
  },
  {
    value: '3',
    label: '3步'
  },
  {
    value: '5',
    label: '5步'
  },
  {
    value: '10',
    label: '10步'
  },
  {
    value: '20',
    label: '20步'
  }
]

/**
 * 提问案例选项
 */
export const exampleOptions = [
  {
    value: '',
    label: '请选择提问案例...'
  },
  {
    value: '1+1',
    label: '1+1'
  },
  {
    value: '2+2',
    label: '2+2'
  },
  {
    value: '检索小傅哥的相关项目，列出一份学习计划',
    label: '检索小傅哥的相关项目，列出一份学习计划'
  },
  {
    value: '根据当前北京互联网程序员加班情况，收入，公司文化等，列出一份大学生推荐入职单位。',
    label: '根据当前北京互联网程序员加班情况，收入，公司文化等，列出一份大学生推荐入职单位。'
  }
]

/**
 * 获取所有选择框配置
 */
export const getSelectOptions = () => {
  return {
    agentOptions,
    maxStepsOptions,
    exampleOptions
  }
}

/**
 * 获取默认值配置
 */
export const getDefaultValues = () => {
  return {
    selectedAgent: '3',
    maxSteps: '5',
    selectedExample: ''
  }
}