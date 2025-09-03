# 🤖 AI Agent Station 数据库设计文档 📊

## 📖 概述

AI Agent Station 数据库采用 MySQL 8.0+ 设计，使用 utf8mb4 字符集，支持完整的 Unicode 字符。数据库主要用于管理 AI 智能体、客户端配置、任务调度等核心功能。

## 🗄️ 数据库信息 💾

- **数据库名称**: `ai-agent-station`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_general_ci`
- **存储引擎**: `InnoDB`

## 📋 表结构设计 🏗️

### 🤖 AI 智能体相关表

#### 1. 🤖 ai_agent - AI智能体配置表

**表描述**: 存储 AI 智能体的基本配置信息

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 主键ID |
| agent_id | varchar(64) | NOT NULL, UNIQUE | - | 智能体ID |
| agent_name | varchar(50) | NOT NULL | - | 智能体名称 |
| description | varchar(255) | NULL | NULL | 描述 |
| channel | varchar(32) | NULL | NULL | 渠道类型(agent，chat_stream) |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_agent_id` (`agent_id`)

#### 2. ⚙️ ai_agent_flow_config - 智能体-客户端关联表

**表描述**: 定义智能体与客户端的关联关系和执行顺序

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 主键ID |
| agent_id | bigint | NOT NULL | - | 智能体ID |
| client_id | bigint | NOT NULL | - | 客户端ID |
| sequence | int | NOT NULL | - | 序列号(执行顺序) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `idx_agent_id` (`agent_id`)
- UNIQUE KEY: `idx_client_id` (`client_id`)
- UNIQUE KEY: `idx_agent_id_client_id` (`agent_id`,`client_id`)
- UNIQUE KEY: `uk_agent_client_seq` (`agent_id`,`client_id`,`sequence`)

#### 3. ⏰ ai_agent_task_schedule - 智能体任务调度配置表

**表描述**: 配置智能体的定时任务调度

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 主键ID |
| agent_id | bigint | NOT NULL | - | 智能体ID |
| task_name | varchar(64) | NULL | NULL | 任务名称 |
| description | varchar(255) | NULL | NULL | 任务描述 |
| cron_expression | varchar(64) | NOT NULL | - | 时间表达式(如: 0/3 * * * * *) |
| task_param | text | NULL | NULL | 任务入参配置(JSON格式) |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- KEY: `idx_agent_id` (`agent_id`)

### 🔧 AI 客户端相关表

#### 4. 💻 ai_client - AI客户端配置表

**表描述**: 存储 AI 客户端的基本配置信息

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 自增主键ID |
| client_id | varchar(64) | NOT NULL, UNIQUE | - | AI客户端ID |
| client_name | varchar(64) | NOT NULL | - | AI客户端名称 |
| description | varchar(1024) | NULL | NULL | 描述 |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_client_id` (`client_id`)

#### 5. 🔧 ai_client_config - AI客户端统一关联配置表

**表描述**: 统一管理客户端与各种资源的关联关系

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 自增主键ID |
| source_type | varchar(32) | NOT NULL | - | 源类型（model、client） |
| source_id | varchar(64) | NOT NULL, UNIQUE | - | 源ID（如 chatModelId、chatClientId 等） |
| target_type | varchar(32) | NOT NULL | - | 目标类型（api、client） |
| target_id | varchar(64) | NOT NULL, UNIQUE | - | 目标ID（如 openAiApiId、chatModelId、systemPromptId、advisorId等 |
| ext_param | varchar(1024) | NULL | NULL | 扩展参数（JSON格式） |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_source_id` (`source_id`)
- UNIQUE KEY: `uk_target_id` (`target_id`)
- INDEX: `idx_source_id_target_id` (`source_id`,`target_id`)

#### 6. 👨‍💼 ai_client_advisor - AI客户端顾问配置表

**表描述**: 配置 AI 客户端的顾问组件

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 自增主键ID |
| advisor_id | varchar(64) | NOT NULL, UNIQUE | - | 顾问ID |
| advisor_name | varchar(64) | NOT NULL | - | 顾问名称 |
| advisor_type | varchar(64) | NOT NULL | - | 顾问类型(PromptChatMemory/RagAnswer/SimpleLoggerAdvisor等) |
| order_num | int | NULL | 0 | 顺序号 |
| ext_param | varchar(2048) | NULL | NULL | 扩展参数配置，json 记录 |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_advisor_id` (`advisor_id`)

#### 7. 🌐 ai_client_api - AI客户端API配置表

**表描述**: 存储 AI 服务的 API 配置信息

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 自增主键ID |
| api_id | varchar(64) | NOT NULL, UNIQUE | - | 全局唯一配置ID |
| base_url | varchar(255) | NOT NULL | - | API基础URL |
| api_key | varchar(255) | NOT NULL | - | API密钥 |
| completions_path | varchar(255) | NOT NULL | - | 补全对话API路径 |
| embeddings_path | varchar(255) | NOT NULL | - | 嵌入向量API路径 |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_api_id` (`api_id`)
- UNIQUE KEY: `uk_status` (`status`)

#### 8. 🧠 ai_client_model - AI客户端模型配置表

**表描述**: 配置 AI 模型信息

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 自增主键ID |
| model_id | varchar(64) | NOT NULL, UNIQUE | - | 全局唯一模型ID |
| api_id | varchar(64) | NOT NULL, UNIQUE | - | 关联的API配置ID -》 表示对应的OpenAiApi使用哪个模型 |
| model_name | varchar(64) | NOT NULL | - | 模型名称（deepseek-r1:14B、qwen3-flash) |
| model_type | varchar(32) | NOT NULL | - | 模型类型：openai、deepseek、claude |
| status | tinyint | NOT NULL | 1 | 状态：0-禁用，1-启用 |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_model_id` (`model_id`)
- UNIQUE KEY: `uk_api_id` (`api_id`)
- UNIQUE KEY: `uk_status` (`status`)

#### 9. 📚 ai_client_rag_order - AI客户端RAG知识库配置表

**表描述**: 配置 RAG 知识库信息

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 主键ID |
| rag_id | varchar(64) | NOT NULL, UNIQUE | - | RAG知识库ID |
| rag_name | varchar(64) | NOT NULL | - | RAG知识库名称 |
| knowledge_tags | varchar(512) | NULL | NULL | 知识库标签 |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_rag_id` (`rag_id`)

#### 10. 💬 ai_client_system_prompt - AI客户端系统提示词配置表

**表描述**: 存储系统提示词配置

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 主键ID |
| prompt_id | varchar(64) | NOT NULL, UNIQUE | - | 系统提示词ID |
| prompt_name | varchar(64) | NOT NULL | - | 系统提示词名称 |
| prompt_content | text | NOT NULL | - | 系统提示词内容 |
| description | varchar(1024) | NULL | NULL | 系统提示词描述 |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_prompt_id` (`prompt_id`)

#### 11. 🛠️ ai_client_tool_mcp - AI客户端MCP工具配置表

**表描述**: 配置 MCP (Model Context Protocol) 工具

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|---------|
| id | bigint | PK, AUTO_INCREMENT | - | 主键ID |
| mcp_id | varchar(64) | NOT NULL, UNIQUE | - | MCP工具ID |
| mcp_name | varchar(64) | NOT NULL | - | MCP工具名称 |
| transport_type | varchar(32) | NOT NULL | - | MCP传输类型：sse/stdio |
| transport_config | varchar(1024) | NULL | NULL | MCP传输配置(sse/stdio config) |
| request_timeout | int | NULL | 180 | MCP请求超时时间(秒) |
| status | tinyint(1) | NULL | 1 | 状态(0:禁用,1:启用) |
| create_time | datetime | NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_mcp_id` (`mcp_id`)

## 🔗 表关系说明 🌐

### 🎯 核心关系

1. **智能体与客户端关系**
   - `ai_agent` ←→ `ai_agent_flow_config` ←→ `ai_client`
   - 一个智能体可以关联多个客户端，通过 `ai_agent_flow_config` 表管理执行顺序

2. **智能体与任务调度关系**
   - `ai_agent` ←→ `ai_agent_task_schedule`
   - 一个智能体可以配置多个定时任务

3. **客户端配置关系**
   - `ai_client` ←→ `ai_client_config` ←→ 各种资源表
   - 通过统一配置表管理客户端与各种资源的关联

4. **API与模型关系**
   - `ai_client_api` ←→ `ai_client_model`
   - 一个API配置对应一个模型配置

### 🏗️ 配置层次结构

```
AI Agent (智能体)
├── Flow Config (流程配置)
│   └── AI Client (客户端)
│       ├── Client Config (统一配置)
│       ├── API Config (API配置)
│       ├── Model Config (模型配置)
│       ├── System Prompt (系统提示词)
│       ├── Advisor (顾问组件)
│       ├── RAG Order (知识库)
│       └── MCP Tool (MCP工具)
└── Task Schedule (任务调度)
```

## 📊 数据字典 📖

### 🚦 状态枚举

| 状态值 | 说明 |
|--------|------|
| 0 | 禁用 |
| 1 | 启用 |

### 📺 渠道类型 (channel)

| 类型值 | 说明 |
|--------|------|
| agent | 智能体模式 |
| chat_stream | 聊天流模式 |

### 🧠 模型类型 (model_type)

| 类型值 | 说明 |
|--------|------|
| openai | OpenAI 模型 |
| deepseek | DeepSeek 模型 |
| claude | Claude 模型 |

### 👨‍💼 顾问类型 (advisor_type)

| 类型值 | 说明 |
|--------|------|
| PromptChatMemory | 提示词聊天记忆 |
| RagAnswer | RAG 回答 |
| SimpleLoggerAdvisor | 简单日志顾问 |

### 🚀 MCP传输类型 (transport_type)

| 类型值 | 说明 |
|--------|------|
| sse | Server-Sent Events |
| stdio | 标准输入输出 |

## 🔧 使用说明 📝

### 🎬 初始化数据库

```bash
# 连接到 MySQL
mysql -u root -p

# 执行 SQL 脚本
source /path/to/ai-agent-station.sql
```

### 🔍 常用查询示例

```sql
-- 查询所有启用的智能体
SELECT * FROM ai_agent WHERE status = 1;

-- 查询智能体关联的客户端
SELECT 
    a.agent_name,
    c.client_name,
    fc.sequence
FROM ai_agent a
JOIN ai_agent_flow_config fc ON a.id = fc.agent_id
JOIN ai_client c ON fc.client_id = c.id
ORDER BY a.agent_name, fc.sequence;

-- 查询客户端的完整配置
SELECT 
    c.client_name,
    api.base_url,
    m.model_name,
    sp.prompt_name
FROM ai_client c
LEFT JOIN ai_client_config cc_api ON c.client_id = cc_api.source_id AND cc_api.target_type = 'api'
LEFT JOIN ai_client_api api ON cc_api.target_id = api.api_id
LEFT JOIN ai_client_config cc_model ON c.client_id = cc_model.source_id AND cc_model.target_type = 'model'
LEFT JOIN ai_client_model m ON cc_model.target_id = m.model_id
LEFT JOIN ai_client_config cc_prompt ON c.client_id = cc_prompt.source_id AND cc_prompt.target_type = 'prompt'
LEFT JOIN ai_client_system_prompt sp ON cc_prompt.target_id = sp.prompt_id;
```

## 📝 注意事项 🚨

1. **字符集**: 所有表都使用 `utf8mb4` 字符集，支持完整的 Unicode 字符，包括 Emoji
2. **时间字段**: 使用 `datetime` 类型，自动维护创建时间和更新时间
3. **状态管理**: 统一使用 `tinyint(1)` 类型管理启用/禁用状态
4. **唯一约束**: 重要的业务ID字段都设置了唯一约束，防止重复
5. **索引优化**: 根据查询需求设置了合适的索引，提高查询性能
6. **扩展性**: 通过 `ext_param` 字段支持 JSON 格式的扩展参数

## 🔄 版本历史 🕐

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2024-01-XX | 初始版本，包含所有核心表结构 |

---

**注意**: 此文档基于当前的 SQL 脚本生成，如有数据库结构变更，请及时更新此文档。