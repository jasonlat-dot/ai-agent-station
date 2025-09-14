# Vue.js 项目 Dockerfile
# 多阶段构建：构建阶段（解决权限） + 运行阶段（稳定运行）

# 构建阶段 - 核心：用非root用户执行所有操作
FROM node:22-alpine AS build-stage

# 1. 创建非root用户（Alpine专用命令，避免root权限问题）
# addgroup/adduser 是Alpine原生工具，创建appgroup组和appuser用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 2. 切换到非root用户（关键！后续所有命令用appuser执行）
USER appuser

# 3. 设置工作目录（使用appuser的家目录，确保有读写权限）
# /home/appuser 是appuser的默认家目录，权限安全（避免/root等敏感目录）
WORKDIR /home/appuser/app

# 4. 复制package文件（指定所有权为appuser，避免root占用）
# --chown=appuser:appgroup 确保复制的文件归新用户所有
COPY --chown=appuser:appgroup package*.json ./

# 6. 安装依赖（appuser执行，避免root权限限制）
# 用npm install（你原配置），若需严格按lockfile安装可换npm ci
RUN npm install

# 7. 复制源代码（同样指定所有权，确保appuser能读写）
COPY --chown=appuser:appgroup . .

# 8. 构建生产版本（手动赋予vue-cli-service执行权限，兜底方案）
# ./node_modules/.bin/ 是npm包可执行文件的默认路径
RUN chmod +x ./node_modules/.bin/vue-cli-service \
    && npm run build

# 生产阶段 - 优化nginx配置，修正路径
FROM nginx:alpine AS production-stage

# 1. 替换Alpine源为国内镜像（加速curl安装，避免IO错误）
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && apk add --no-cache curl

# 2. 复制自定义nginx配置（修正路径！避免../上级目录，确保相对于构建上下文）
# 前提：software目录与Dockerfile在同一级（目录结构见下方说明）
COPY software/nginx.conf /etc/nginx/conf.d/default.conf

# 3. 复制构建产物（从build-stage的appuser目录复制，权限正确）
COPY --from=build-stage /home/appuser/app/dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80

# 启动nginx（前台运行，确保容器不退出）
CMD ["nginx", "-g", "daemon off;"]