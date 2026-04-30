# ============================================
# Stage 1: Node Build
# ============================================
FROM node:18-alpine AS builder

WORKDIR /build

# 先复制依赖文件，利用 Docker 缓存加速
COPY package.json package-lock.json* ./
RUN npm install

# 复制源代码并构建
COPY . .
RUN npm run build

# ============================================
# Stage 2: Nginx Runtime
# ============================================
FROM nginx:1.25-alpine

LABEL maintainer="polikm"
LABEL description="学情数据统计分析平台 - 前端服务"

# 移除默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 复制构建产物
COPY --from=builder /build/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
