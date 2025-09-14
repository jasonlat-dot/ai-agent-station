/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : localhost:13306
 Source Schema         : ai-agent-station

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 13/09/2025 23:26:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `ai-agent-station` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
use `ai-agent-station`;

-- ----------------------------
-- Table structure for ai_agent
-- ----------------------------
DROP TABLE IF EXISTS `ai_agent`;
CREATE TABLE `ai_agent`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '智能体ID',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '智能体名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道类型(agent，chat_stream)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_agent_id`(`agent_id` ) USING BTREE,
  INDEX `idx_agent_id_status`(`agent_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI智能体配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_agent
-- ----------------------------
INSERT INTO `ai_agent` VALUES (1, '1', '自动发帖服务01', 'CSDN自动发帖，微信公众号通知。', 'agent', 1, '2025-09-13 22:26:31', '2025-09-13 22:26:31');
INSERT INTO `ai_agent` VALUES (2, '2', '智能对话体（MCP）', '自动发帖，工具服务', 'chat_stream', 1, '2025-09-13 22:26:31', '2025-09-13 22:26:31');
INSERT INTO `ai_agent` VALUES (3, '3', '智能对话体（Auto)', '自动分析和执行任务', 'agent', 1, '2025-09-13 22:26:31', '2025-09-13 22:26:31');

-- ----------------------------
-- Table structure for ai_agent_flow_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_agent_flow_config`;
CREATE TABLE `ai_agent_flow_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '智能体ID',
  `client_id` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
  `client_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端名称',
  `client_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端类型',
  `sequence` int NOT NULL COMMENT '序列号(执行顺序)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_agent_client_seq`(`agent_id`, `client_id`, `sequence`) USING BTREE,
  INDEX `idx_agent_id`(`agent_id`) USING BTREE,
  INDEX `idx_client_id`(`client_id`) USING BTREE,
  INDEX `idx_agent_id_client_id`(`agent_id`, `client_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '智能体-客户端关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_agent_flow_config
-- ----------------------------
INSERT INTO `ai_agent_flow_config` VALUES (1, '1', '3001', '通用的', 'DEFAULT', 1, '2025-09-13 22:26:31');
INSERT INTO `ai_agent_flow_config` VALUES (2, '3', '3101', '任务分析和状态判断', 'TASK_ANALYZER_CLIENT', 1, '2025-09-13 22:26:31');
INSERT INTO `ai_agent_flow_config` VALUES (3, '3', '3102', '具体任务执行', 'PRECISION_EXECUTOR_CLIENT', 2, '2025-09-13 22:26:31');
INSERT INTO `ai_agent_flow_config` VALUES (4, '3', '3103', '质量检查和优化', 'QUALITY_SUPERVISOR_CLIENT', 3, '2025-09-13 22:26:31');
INSERT INTO `ai_agent_flow_config` VALUES (5, '3', '3104', '负责响应式处理', 'RESPONSE_ASSISTANT', 4, '2025-09-13 22:40:24');

-- ----------------------------
-- Table structure for ai_agent_task_schedule
-- ----------------------------
DROP TABLE IF EXISTS `ai_agent_task_schedule`;
CREATE TABLE `ai_agent_task_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '智能体ID',
  `task_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务描述',
  `cron_expression` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '时间表达式(如: 0/3 * * * * *)',
  `task_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务入参配置(JSON格式)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_agent_id`(`agent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '智能体任务调度配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_agent_task_schedule
-- ----------------------------
INSERT INTO `ai_agent_task_schedule` VALUES (1, '1', '自动发帖', '自动发帖和通知', '0 0/30 * * * ?', '发布CSDN文章', 1, '2025-09-13 22:26:31', '2025-09-13 22:26:31');

-- ----------------------------
-- Table structure for ai_client
-- ----------------------------
DROP TABLE IF EXISTS `ai_client`;
CREATE TABLE `ai_client`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'AI客户端ID',
  `client_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'AI客户端名称',
  `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_client_id`(`client_id` ) USING BTREE,
  INDEX `idx_client_id_status`(`client_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client
-- ----------------------------
INSERT INTO `ai_client` VALUES (1, '3001', '提示词优化', '提示词优化，分为角色、动作、规则、目标等。', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (2, '3002', '自动发帖和通知', '自动生成CSDN文章，发送微信公众号消息通知', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (3, '3003', '文件操作服务', '文件操作服务', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (4, '3004', '流式对话客户端', '流式对话客户端', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (5, '3005', '地图', '地图', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (6, '3101', '任务分析和状态判断', '你是一个专业的任务分析师，名叫 AutoAgent Task Analyzer。', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (7, '3102', '具体任务执行', '你是一个精准任务执行器，名叫 AutoAgent Precision Executor。', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (8, '3103', '质量检查和优化', '你是一个专业的质量监督员，名叫 AutoAgent Quality Supervisor。', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client` VALUES (9, '3104', '负责响应式处理', '你是一个智能响应助手，名叫 AutoAgent React。', 1, '2025-09-13 22:48:16', '2025-09-13 22:48:16');

-- ----------------------------
-- Table structure for ai_client_advisor
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_advisor`;
CREATE TABLE `ai_client_advisor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `advisor_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '顾问ID',
  `advisor_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '顾问名称',
  `advisor_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '顾问类型(PromptChatMemory/RagAnswer/SimpleLoggerAdvisor等)',
  `order_num` int NULL DEFAULT 0 COMMENT '顺序号',
  `ext_param` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '扩展参数配置，json 记录',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_advisor_id`(`advisor_id` ) USING BTREE,
  INDEX `idx_advisor_id_status`(`advisor_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端顾问配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client_advisor
-- ----------------------------
INSERT INTO `ai_client_advisor` VALUES (1, '4001', '记忆', 'ChatMemory', 1, '{\n    \"maxMessages\": 200\n}', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_advisor` VALUES (2, '4002', '访问文章提示词知识库', 'RagAnswer', 1, '{\n    \"topK\": \"4\",\n    \"filterExpression\": \"knowledge == \'知识库名称\'\"\n}', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');

-- ----------------------------
-- Table structure for ai_client_api
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_api`;
CREATE TABLE `ai_client_api`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `api_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '全局唯一配置ID',
  `base_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API基础URL',
  `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API密钥',
  `completions_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '补全对话API路径',
  `embeddings_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '嵌入向量API路径',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_api_id`(`api_id` ) USING BTREE,
  INDEX `idx_api_id_status`(`api_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端API配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client_api
-- ----------------------------
INSERT INTO `ai_client_api` VALUES (1, '1001', 'https://dashscope.aliyuncs.com/compatible-mode', 'sk-382688e6c9184dea8715dfbfdb241355', NULL, NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_api` VALUES (2, '1002', 'https://open.bigmodel.cn/api/paas', 'c3e3912426c49a7b990280b1d2d235a3.tcgJOlShzn6ba6uk', '/v4/chat/completions', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:45:18');

-- ----------------------------
-- Table structure for ai_client_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_config`;
CREATE TABLE `ai_client_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `source_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源类型（model、client）',
  `source_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源ID（如 chatModelId、chatClientId 等）',
  `target_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型（api、client）',
  `target_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标ID（如 openAiApiId、chatModelId、systemPromptId、advisorId等',
  `ext_param` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '扩展参数（JSON格式）',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_source_id_target_id`(`source_id` , `target_id` ) USING BTREE,
  INDEX `idx_source_id_target_id_status`(`source_id` , `target_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端统一关联配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client_config
-- ----------------------------
INSERT INTO `ai_client_config` VALUES (1, 'model', '2001', 'tool_mcp', '5001', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (2, 'model', '2001', 'tool_mcp', '5002', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (3, 'model', '2001', 'tool_mcp', '5003', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (4, 'model', '2001', 'tool_mcp', '5004', NULL, 0, '2025-09-13 22:26:32', '2025-09-13 22:28:57');
INSERT INTO `ai_client_config` VALUES (5, 'client', '3001', 'advisor', '4001', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (6, 'client', '3001', 'prompt', '6001', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (7, 'client', '3001', 'prompt', '6002', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (8, 'client', '3001', 'model', '2001', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (9, 'model', '2001', 'tool_mcp', '5006', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (10, 'client', '3101', 'model', '2002', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:37:38');
INSERT INTO `ai_client_config` VALUES (11, 'client', '3101', 'prompt', '6101', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (12, 'client', '3101', 'advisor', '4001', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (13, 'client', '3101', 'tool_mcp', '5006', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (14, 'client', '3102', 'model', '2002', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:37:38');
INSERT INTO `ai_client_config` VALUES (15, 'client', '3102', 'prompt', '6102', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (16, 'client', '3102', 'advisor', '4001', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (17, 'client', '3102', 'tool_mcp', '5006', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (18, 'client', '3103', 'model', '2002', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:37:38');
INSERT INTO `ai_client_config` VALUES (19, 'client', '3103', 'prompt', '6103', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (20, 'client', '3103', 'advisor', '4001', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (21, 'client', '3103', 'tool_mcp', '5006', NULL, 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_config` VALUES (22, 'client', '3104', 'model', '2002', NULL, 1, '2025-09-13 22:37:13', '2025-09-13 22:37:13');
INSERT INTO `ai_client_config` VALUES (23, 'client', '3104', 'prompt', '6104', NULL, 1, '2025-09-13 22:37:13', '2025-09-13 22:37:13');

-- ----------------------------
-- Table structure for ai_client_model
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_model`;
CREATE TABLE `ai_client_model`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `model_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '全局唯一模型ID',
  `api_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联的API配置ID -》 表示对应的OpenAiApi使用哪个模型',
  `model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型名称（deepseek-r1:14B、qwen-flash)',
  `model_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型类型：openai、deepseek、claude',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_model_name_api_id`(`model_name` , `api_id` ) USING BTREE,
  INDEX `idx_model_id`(`model_id` ) USING BTREE,
  INDEX `idx_api_id`(`api_id` ) USING BTREE,
  INDEX `idx_model_id_status`(`model_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端模型配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client_model
-- ----------------------------
INSERT INTO `ai_client_model` VALUES (1, '2001', '1001', 'qwen-flash-2025-07-28', 'qwen3_openai', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');
INSERT INTO `ai_client_model` VALUES (2, '2002', '1002', 'GLM-4.5-Flash', 'glm_zhipu', 1, '2025-09-13 22:26:32', '2025-09-13 22:26:32');

-- ----------------------------
-- Table structure for ai_client_rag_order
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_rag_order`;
CREATE TABLE `ai_client_rag_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rag_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'RAG知识库ID',
  `rag_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'RAG知识库名称',
  `knowledge_tags` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '知识库标签',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rag_id`(`rag_id` ) USING BTREE,
  INDEX `idx_rag_id_status`(`rag_id` , `status` ) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端RAG知识库配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client_rag_order
-- ----------------------------

-- ----------------------------
-- Table structure for ai_client_system_prompt
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_system_prompt`;
CREATE TABLE `ai_client_system_prompt`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `prompt_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统提示词ID',
  `prompt_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统提示词名称',
  `prompt_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统提示词内容',
  `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统提示词描述',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_prompt_id`(`prompt_id` ) USING BTREE,
  INDEX `idx_prompt_id_status`(`prompt_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端系统提示词配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client_system_prompt
-- ----------------------------
INSERT INTO `ai_client_system_prompt` VALUES (6, '6001', '提示词优化', '你是一个专业的AI提示词优化专家。请帮我优化以下prompt，并按照以下格式返回：\n\n# Role: [角色名称]\n\n## Profile\n\n- language: [语言]\n- description: [详细的角色描述]\n- background: [角色背景]\n- personality: [性格特征]\n- expertise: [专业领域]\n- target_audience: [目标用户群]\n\n## Skills\n\n1. [核心技能类别]\n   - [具体技能]: [简要说明]\n   - [具体技能]: [简要说明]\n   - [具体技能]: [简要说明]\n   - [具体技能]: [简要说明]\n2. [辅助技能类别]\n   - [具体技能]: [简要说明]\n   - [具体技能]: [简要说明]\n   - [具体技能]: [简要说明]\n   - [具体技能]: [简要说明]\n\n## Rules\n\n1. [基本原则]：\n   - [具体规则]: [详细说明]\n   - [具体规则]: [详细说明]\n   - [具体规则]: [详细说明]\n   - [具体规则]: [详细说明]\n2. [行为准则]：\n   - [具体规则]: [详细说明]\n   - [具体规则]: [详细说明]\n   - [具体规则]: [详细说明]\n   - [具体规则]: [详细说明]\n3. [限制条件]：\n   - [具体限制]: [详细说明]\n   - [具体限制]: [详细说明]\n   - [具体限制]: [详细说明]\n   - [具体限制]: [详细说明]\n\n## Workflows\n\n- 目标: [明确目标]\n- 步骤 1: [详细说明]\n- 步骤 2: [详细说明]\n- 步骤 3: [详细说明]\n- 预期结果: [说明]\n\n## Initialization\n\n作为[角色名称]，你必须遵守上述Rules，按照Workflows执行任务。\n请基于以上模板，优化并扩展以下prompt，确保内容专业、完整且结构清晰，注意不要携带任何引导词或解释，不要使用代码块包围。', '提示词优化，拆分执行动作', 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_system_prompt` VALUES (7, '6002', '发帖和消息通知介绍', '你是一个 AI Agent 智能体，可以根据用户输入信息生成文章，并发送到 CSDN 平台以及完成微信公众号消息通知，今天是 {current_date}。\n\n你擅长使用Planning模式，帮助用户生成质量更高的文章。\n\n你的规划应该包括以下几个方面：\n1. 分析用户输入的内容，生成技术文章。\n2. 提取，文章标题（需要含带技术点）、文章内容、文章标签（多个用英文逗号隔开）、文章简述（100字）将以上内容发布文章到CSDN\n3. 获取发送到 CSDN 文章的 URL 地址。\n4. 微信公众号消息通知，平台：CSDN、主题：为文章标题、描述：为文章简述、跳转地址：为发布文章到CSDN获取 URL地址 CSDN文章链接 https 开头的地址。', '提示词优化，拆分执行动作', 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_system_prompt` VALUES (8, '6003', 'CSDN发布文章', '我需要你帮我生成一篇文章，要求如下；\n                                \n                1. 场景为互联网大厂java求职者面试\n                2. 面试管提问 Java 核心知识、JUC、JVM、多线程、线程池、HashMap、ArrayList、Spring、SpringBoot、MyBatis、Dubbo、RabbitMQ、xxl-job、Redis、MySQL、Linux、Docker、设计模式、DDD等不限于此的各项技术问题。\n                3. 按照故事场景，以严肃的面试官和搞笑的水货程序员谢飞机进行提问，谢飞机对简单问题可以回答，回答好了面试官还会夸赞。复杂问题胡乱回答，回答的不清晰。\n                4. 每次进行3轮提问，每轮可以有3-5个问题。这些问题要有技术业务场景上的衔接性，循序渐进引导提问。最后是面试官让程序员回家等通知类似的话术。\n                5. 提问后把问题的答案，写到文章最后，最后的答案要详细讲述出技术点，让小白可以学习下来。\n                                \n                根据以上内容，不要阐述其他信息，请直接提供；文章标题、文章内容、文章标签（多个用英文逗号隔开）、文章简述（100字）\n                                \n                将以上内容发布文章到CSDN。', 'CSDN发布文章', 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_system_prompt` VALUES (9, '6004', '文章操作测试', '在 C:\\Users\\Administrator\\Desktop 创建文件 file01.txt', '文件操作测试', 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_system_prompt` VALUES (10, '6101', '负责任务分析和状态判断', '# 角色\n你是一个专业的任务分析师，名叫 AutoAgent Task Analyzer。\n# 核心职责\n你负责分析任务的当前状态、执行历史和下一步行动计划：\n1. **状态分析**: 深度分析当前任务完成情况和执行历史\n2. **进度评估**: 评估任务完成进度和质量\n3. **策略制定**: 制定下一步最优执行策略\n4. **完成判断**: 准确判断任务是否已完成\n# 分析原则\n- **全面性**: 综合考虑所有执行历史和当前状态\n- **准确性**: 准确评估任务完成度和质量\n- **前瞻性**: 预测可能的问题和最优路径\n- **效率性**: 优化执行路径，避免重复工作\n# 输出格式\n**任务状态分析:**\n[当前任务完成情况的详细分析]\n**执行历史评估:**\n[对已完成工作的质量和效果评估]\n**下一步策略:**\n[具体的下一步执行计划和策略]\n**完成度评估:** [0-100]%\n**任务状态:** [CONTINUE/COMPLETED]', '负责任务分析和状态判断的提示词', 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_system_prompt` VALUES (11, '6102', '负责具体任务执行', '# 角色\n你是一个精准任务执行器，名叫 AutoAgent Precision Executor。\n# 核心能力\n你专注于精准执行具体的任务步骤：\n1. **精准执行**: 严格按照分析师的策略执行任务\n2. **工具使用**: 熟练使用各种工具完成复杂操作\n3. **质量控制**: 确保每一步执行的准确性和完整性\n4. **结果记录**: 详细记录执行过程和结果\n# 执行原则\n- **专注性**: 专注于当前分配的具体任务\n- **精准性**: 确保执行结果的准确性和质量\n- **完整性**: 完整执行所有必要的步骤\n- **可追溯性**: 详细记录执行过程便于后续分析\n# 输出格式\n**执行目标:**\n[本轮要执行的具体目标]\n**执行过程:**\n[详细的执行步骤和使用的工具]\n**执行结果:**\n[执行的具体结果和获得的信息]\n**质量检查:**\n[对执行结果的质量评估]', '负责具体任务执行的提示词', 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_system_prompt` VALUES (12, '6103', '负责质量检查和优化', '# 角色\n你是一个专业的质量监督员，名叫 AutoAgent Quality Supervisor。\n# 核心职责\n你负责监督和评估执行质量：\n1. **质量评估**: 评估执行结果的准确性和完整性\n2. **问题识别**: 识别执行过程中的问题和不足\n3. **改进建议**: 提供具体的改进建议和优化方案\n4. **标准制定**: 制定质量标准和评估指标\n# 评估标准\n- **准确性**: 结果是否准确无误\n- **完整性**: 是否遗漏重要信息\n- **相关性**: 是否符合用户需求\n- **可用性**: 结果是否实用有效\n# 输出格式\n**质量评估:**\n[对执行结果的详细质量评估]\n**问题识别:**\n[发现的问题和不足之处]\n**改进建议:**\n[具体的改进建议和优化方案]\n**质量评分:** [0-100]分\n**是否通过:** [PASS/FAIL/OPTIMIZE]', '负责质量检查和优化的提示词', 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_system_prompt` VALUES (13, '6104', '智能响应助手', '# 角色\\n你是一个专业的质量监督员，名叫 AutoAgent Quality Supervisor。\\n# 核心职责\\n你负责监督和评估执行质量：\\n1. **质量评估**: 评估执行结果的准确性和完整性\\n2. **问题识别**: 识别执行过程中的问题和不足\\n3. **改进建议**: 提供具体的改进建议和优化方案\\n4. **标准制定**: 制定质量标准和评估指标\\n# 评估标准\\n- **准确性**: 结果是否准确无误\\n- **完整性**: 是否遗漏重要信息\\n- **相关性**: 是否符合用户需求\\n- **可用性**: 结果是否实用有效\\n# 输出格式\\n**质量评估:**\\n[对执行结果的详细质量评估]\\n**问题识别:**\\n[发现的问题和不足之处]\\n**改进建议:**\\n[具体的改进建议和优化方案]\\n**质量评分:** [0-100]分\\n**是否通过:** [PASS/FAIL/OPTIMIZE]', '负责质量检查和优化', 1, '2025-09-13 22:32:40', '2025-09-13 22:32:40');

-- ----------------------------
-- Table structure for ai_client_tool_mcp
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_tool_mcp`;
CREATE TABLE `ai_client_tool_mcp`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mcp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'MCP工具ID',
  `mcp_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'MCP工具名称',
  `transport_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'MCP传输类型：sse/stdio',
  `transport_config` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'MCP传输配置(sse/stdio config)',
  `request_timeout` int NULL DEFAULT 180 COMMENT 'MCP请求超时时间(秒)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_mcp_id`(`mcp_id` ) USING BTREE,
  INDEX `idx_mcp_id_status`(`mcp_id` , `status` ) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI客户端MCP工具配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_client_tool_mcp
-- ----------------------------
INSERT INTO `ai_client_tool_mcp` VALUES (6, '5001', 'CSDN自动发帖', 'sse', '{\n	\"baseUri\":\"http://127.0.0.1:8101\",\n        \"sseEndpoint\":\"/sse\"\n}', 180, 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_tool_mcp` VALUES (7, '5002', '微信公众号消息通知', 'sse', '{\n	\"baseUri\":\"http://127.0.0.1:8102\",\n        \"sseEndpoint\":\"/sse\"\n}', 180, 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_tool_mcp` VALUES (8, '5003', 'filesystem', 'stdio', '{\n    \"filesystem\": {\n        \"command\": \"C:\\\\Users\\\\Administrator\\\\AppData\\\\Roaming\\\\npm\\\\npx.cmd\",\n        \"args\": [\n            \"-y\",\n            \"@modelcontextprotocol/server-filesystem\",\n            \"C:\\\\Users\\\\Administrator\\\\Desktop\",\n            \"C:\\\\Users\\\\Administrator\\\\Desktop\"\n        ]\n    }\n}', 180, 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_tool_mcp` VALUES (9, '5004', 'g-search', 'stdio', '{\n    \"g-search\": {\n        \"command\": \"npx\",\n        \"args\": [\n            \"-y\",\n            \"g-search-mcp\"\n        ]\n    }\n}', 180, 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_tool_mcp` VALUES (10, '5005', '高德地图', 'sse', '{\n	\"baseUri\":\"https://mcp.amap.com\",\n        \"sseEndpoint\":\"/sse?key=801aabf79ed055c2ff78603cfe851787\"\n}', 180, 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');
INSERT INTO `ai_client_tool_mcp` VALUES (11, '5006', 'baidu-search', 'sse', '{\n	\"baseUri\":\"http://appbuilder.baidu.com/v2/ai_search/mcp/\",\n        \"sseEndpoint\":\"sse?api_key=bce-v3/ALTAK-DAjqmKUyljHZzKsZnJ8x6/88724397eb336d831b13364919cbade256c459a1\"\n}', 180, 1, '2025-09-13 22:26:33', '2025-09-13 22:26:33');

SET FOREIGN_KEY_CHECKS = 1;
