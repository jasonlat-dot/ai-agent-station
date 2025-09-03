# AI Agent Station 🤖

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub](https://img.shields.io/badge/GitHub-jasonlat--dot-black.svg)](https://github.com/jasonlat-dot)

## 📖 项目简介

AI Agent Station 是一个基于 Spring Boot 3.x 和 Java 17 构建的智能代理站点系统。该项目采用领域驱动设计（DDD）架构模式，提供了一个可扩展、高性能的个人AiAgent项目。

## 🛠️ 技术栈

### 🔧 核心技术
- **Java 17** - 编程语言
- **Spring Boot 3.4.3** - 应用框架
- **Maven** - 项目管理和构建工具
- **MyBatis Plus 3.5.5** - 数据持久化框架
- **MySQL 8.x** - 关系型数据库
- **Redis** - 缓存和分布式锁
- **RabbitMQ** - 消息队列

### 🔨 辅助工具
- **Lombok** - 简化Java代码
- **FastJSON 2.0.28** - JSON处理
- **Guava 32.1.3** - Google核心库
- **Apache Commons Lang3** - 工具类库
- **Hutool** - Java工具包
- **Docker** - 容器化部署

## 🏗️ 项目架构

本项目采用DDD（领域驱动设计）分层架构，模块划分如下：

```
ai-agent-station/
├── ai-agent-station-api/          # API接口层 - 对外接口定义
├── ai-agent-station-app/          # 应用启动层 - 应用程序入口
├── ai-agent-station-domain/       # 领域层 - 核心业务逻辑
├── ai-agent-station-infrastructure/# 基础设施层 - 数据访问和外部服务
├── ai-agent-station-trigger/      # 触发器层 - 控制器和事件处理
├── ai-agent-station-types/        # 类型定义层 - 通用类型和工具
└── dev-ops/                       # 运维配置 - Docker和部署脚本
```

### 📋 模块职责

#### ai-agent-station-api
- 定义对外暴露的API接口
- 包含请求和响应的数据传输对象（DTO）
- 提供接口文档和规范

#### ai-agent-station-app
- 应用程序启动入口
- Spring Boot主配置
- 全局配置管理
- 线程池配置

#### ai-agent-station-domain
- 核心业务逻辑实现
- 领域模型和聚合根
- 业务规则和领域服务
- 领域事件处理

#### ai-agent-station-infrastructure
- 数据持久化实现
- 外部服务集成
- 消息队列处理
- 缓存管理

#### ai-agent-station-trigger
- HTTP控制器
- 定时任务
- 事件监听器
- 外部触发器处理

#### ai-agent-station-types
- 通用数据类型定义
- 工具类和帮助方法
- 常量定义
- 异常类型

## 🚀 快速开始

### 📋 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+

### 💻 本地开发

1. **克隆项目**
   ```bash
   git clone https://github.com/jasonlat-dot/ai-agent-station.git
   cd ai-agent-station
   ```

2. **配置数据库**
   - 创建MySQL数据库
   - 配置数据库连接信息
   - 执行数据库初始化脚本

3. **配置Redis和RabbitMQ**
   - 启动Redis服务
   - 启动RabbitMQ服务
   - 配置连接参数

4. **编译项目**
   ```bash
   mvn clean compile
   ```

5. **运行应用**
   ```bash
   mvn spring-boot:run -pl ai-agent-station-app
   ```

### 🐳 Docker部署

1. **构建镜像**
   ```bash
   cd dev-ops/software
   chmod +x build.sh
   ./build.sh
   ```

2. **运行容器**
   ```bash
   docker run -d -p 8080:8080 jasonlat/ai-agent-station-app:1.0
   ```

## 📝 开发规范

### 📏 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一使用UTF-8编码
- 添加必要的注释和文档

### 🌿 分支管理
- `main` - 主分支，用于生产环境
- `develop` - 开发分支，用于功能集成
- `feature/*` - 功能分支，用于新功能开发
- `hotfix/*` - 热修复分支，用于紧急修复

### 📝 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## ⚙️ 配置说明

### 🗄️ 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ai_agent_station?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 🔴 Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
```

### 🐰 RabbitMQ配置
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: your_username
    password: your_password
```

## 🗄️ 数据库设计

### 📋 数据库文件说明

项目的数据库相关文件位于 `dev-ops/mysql/` 目录下：

#### 📄 核心文件
- **[`ai-agent-station.sql`](dev-ops/mysql/ai-agent-station.sql)** - 数据库建表脚本
  - 包含完整的数据库创建语句
  - 支持 MySQL 8.0+ 版本
  - 使用 utf8mb4 字符集
  - 包含所有表结构、索引和约束定义

- **[`sql.md`](dev-ops/mysql/sql.md)** - 数据库设计文档
  - 详细的表结构说明
  - 字段定义和约束说明
  - 索引设计说明
  - 业务逻辑说明

#### 📊 ER图文件
- **[`er-diagram.md`](dev-ops/mysql/er-diagram.md)** - Mermaid格式ER图
  - 支持在GitHub、GitLab等平台直接渲染
  - 可在线编辑：https://mermaid.live/
  
- **[`er-diagram.puml`](dev-ops/mysql/er-diagram.puml)** - PlantUML格式ER图
  - 支持生成PNG、SVG、PDF等格式
  - 在线渲染：http://www.plantuml.com/plantuml/
  
- **[`er-diagram.svg`](dev-ops/mysql/er-diagram.svg)** - SVG格式ER图
  - 矢量图形，可无限缩放
  - 直接在浏览器中查看

### 🏗️ 数据库架构

#### 🤖 AI智能体模块
- `ai_agent` - AI智能体配置表
- `ai_agent_flow_config` - 智能体-客户端关联表
- `ai_agent_task_schedule` - 智能体任务调度配置表

#### 💻 AI客户端模块
- `ai_client` - AI客户端配置表
- `ai_client_config` - AI客户端统一关联配置表
- `ai_client_advisor` - AI客户端顾问配置表
- `ai_client_api` - AI客户端API配置表
- `ai_client_model` - AI客户端模型配置表
- `ai_client_system_prompt` - AI客户端系统提示配置表
- `ai_client_rag_order` - AI客户端RAG订单表
- `ai_client_tool_mcp` - AI客户端MCP工具配置表

### 🚀 数据库初始化

1. **创建数据库**
   ```sql
   -- 执行建表脚本
   source dev-ops/mysql/ai-agent-station.sql
   ```

2. **验证安装**
   ```sql
   USE ai_agent_station;
   SHOW TABLES;
   ```

### 📝 日志配置
- 使用SLF4J + Logback进行日志管理
- 日志文件位置: `./data/log/`
- 支持按日期和大小滚动

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 Apache License 2.0 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

## 📞 联系方式

- 作者: jasonlat
- 邮箱: 2148660566@qq.com
- GitHub: https://github.com/jasonlat-dot
- 项目地址: https://github.com/jasonlat-dot/ai-agent-station

## 📅 更新日志

### v1.0-SNAPSHOT
- 初始版本发布
- 基础架构搭建
- 核心模块实现
- Docker支持

---

**注意**: 这是一个初始化工程，具体的业务功能正在开发中。如有问题或建议，欢迎提交Issue或Pull Request。