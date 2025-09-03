
CREATE database if NOT EXISTS `ai-agent-station` default character set utf8mb4 collate utf8mb4_general_ci;
use `ai-agent-station`;

DROP TABLE IF EXISTS `ai_agent`;
CREATE TABLE `ai_agent` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id` varchar(64) NOT NULL COMMENT '智能体ID',
    `agent_name` varchar(50) NOT NULL COMMENT '智能体名称',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `channel` varchar(32) DEFAULT NULL COMMENT '渠道类型(agent，chat_stream)',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI智能体配置表';



DROP TABLE IF EXISTS `ai_agent_flow_config`;
CREATE TABLE `ai_agent_flow_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id` bigint NOT NULL COMMENT '智能体ID',
    `client_id` bigint NOT NULL COMMENT '客户端ID',
    `sequence` int NOT NULL COMMENT '序列号(执行顺序)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_agent_id` (`agent_id`),
    UNIQUE KEY `idx_client_id` (`client_id`),
    UNIQUE KEY `idx_agent_id_client_id` (`agent_id`,`client_id`),
    UNIQUE KEY `uk_agent_client_seq` (`agent_id`,`client_id`,`sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='智能体-客户端关联表';



DROP TABLE IF EXISTS `ai_agent_task_schedule`;
CREATE TABLE `ai_agent_task_schedule` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id` bigint NOT NULL COMMENT '智能体ID',
    `task_name` varchar(64) DEFAULT NULL COMMENT '任务名称',
    `description` varchar(255) DEFAULT NULL COMMENT '任务描述',
    `cron_expression` varchar(64) NOT NULL COMMENT '时间表达式(如: 0/3 * * * * *)',
    `task_param` text COMMENT '任务入参配置(JSON格式)',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='智能体任务调度配置表';



DROP TABLE IF EXISTS `ai_client`;
CREATE TABLE `ai_client` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
    `client_id` varchar(64) NOT NULL COMMENT 'AI客户端ID',
    `client_name` varchar(64) NOT NULL COMMENT 'AI客户端名称',
    `description` varchar(1024) DEFAULT NULL COMMENT '描述',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端配置表';



DROP TABLE IF EXISTS `ai_client_config`;
CREATE TABLE `ai_client_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
    `source_type` varchar(32) NOT NULL COMMENT '源类型（model、client）',
    `source_id` varchar(64) NOT NULL COMMENT '源ID（如 chatModelId、chatClientId 等）',
    `target_type` varchar(32) NOT NULL COMMENT '目标类型（api、client）',
    `target_id` varchar(64) NOT NULL COMMENT '目标ID（如 openAiApiId、chatModelId、systemPromptId、advisorId等',
    `ext_param` varchar(1024) DEFAULT NULL COMMENT '扩展参数（JSON格式）',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_source_id` (`source_id`),
    UNIQUE KEY `uk_target_id` (`target_id`),
    INDEX `idx_source_id_target_id` (`source_id`,`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端统一关联配置表';



DROP TABLE IF EXISTS `ai_client_advisor`;
CREATE TABLE `ai_client_advisor` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
    `advisor_id` varchar(64) NOT NULL COMMENT '顾问ID',
    `advisor_name` varchar(64) NOT NULL COMMENT '顾问名称',
    `advisor_type` varchar(64) NOT NULL COMMENT '顾问类型(PromptChatMemory/RagAnswer/SimpleLoggerAdvisor等)',
    `order_num` int DEFAULT '0' COMMENT '顺序号',
    `ext_param` varchar(2048) DEFAULT NULL COMMENT '扩展参数配置，json 记录',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_advisor_id` (`advisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端顾问配置表';



DROP TABLE IF EXISTS `ai_client_api`;
CREATE TABLE `ai_client_api` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
    `api_id` varchar(64) NOT NULL COMMENT '全局唯一配置ID',
    `base_url` varchar(255) NOT NULL COMMENT 'API基础URL',
    `api_key` varchar(255) NOT NULL COMMENT 'API密钥',
    `completions_path` varchar(255) NOT NULL COMMENT '补全对话API路径',
    `embeddings_path` varchar(255) NOT NULL COMMENT '嵌入向量API路径',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_api_id` (`api_id`),
    UNIQUE KEY `uk_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端API配置表';



DROP TABLE IF EXISTS `ai_client_model`;
CREATE TABLE `ai_client_model` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
    `model_id` varchar(64) NOT NULL COMMENT '全局唯一模型ID',
    `api_id` varchar(64) NOT NULL COMMENT '关联的API配置ID -》 表示对应的OpenAiApi使用哪个模型',
    `model_name` varchar(64) NOT NULL COMMENT '模型名称（deepseek-r1:14B、qwen3-flash)',
    `model_type` varchar(32) NOT NULL COMMENT '模型类型：openai、deepseek、claude',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_id` (`model_id`),
    UNIQUE KEY `uk_api_id` (`api_id`),
    UNIQUE KEY `uk_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端模型配置表';



DROP TABLE IF EXISTS `ai_client_rag_order`;
CREATE TABLE `ai_client_rag_order` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `rag_id` varchar(64) NOT NULL COMMENT 'RAG知识库ID',
    `rag_name` varchar(64) NOT NULL COMMENT 'RAG知识库名称',
    `knowledge_tags` varchar(512) DEFAULT NULL COMMENT '知识库标签',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rag_id` (`rag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端RAG知识库配置表';



DROP TABLE IF EXISTS `ai_client_system_prompt`;
CREATE TABLE `ai_client_system_prompt` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `prompt_id` varchar(64) NOT NULL COMMENT '系统提示词ID',
    `prompt_name` varchar(64) NOT NULL COMMENT '系统提示词名称',
    `prompt_content` text NOT NULL COMMENT '系统提示词内容',
    `description` varchar(1024) DEFAULT NULL COMMENT '系统提示词描述',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_prompt_id` (`prompt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端系统提示词配置表';



DROP TABLE IF EXISTS `ai_client_tool_mcp`;
CREATE TABLE `ai_client_tool_mcp` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `mcp_id` varchar(64) NOT NULL COMMENT 'MCP工具ID',
    `mcp_name` varchar(64) NOT NULL COMMENT 'MCP工具名称',
    `transport_type` varchar(32) NOT NULL COMMENT 'MCP传输类型：sse/stdio',
    `transport_config` varchar(1024) DEFAULT NULL COMMENT 'MCP传输配置(sse/stdio config)',
    `request_timeout` int DEFAULT '180' COMMENT 'MCP请求超时时间(秒)',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_mcp_id` (`mcp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI客户端MCP工具配置表';

