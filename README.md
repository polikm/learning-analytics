# 学情数据统计分析平台 (Learning Analytics Platform)

一款面向学校、教师、家长和学生的全栈学情数据分析平台，支持在线测评、成绩追踪、学情档案、预警分析、证书管理和报告生成等功能。

## 功能特性

### 🎓 测评系统
- **在线测评**：支持单选题、多选题、判断题、填空题、简答题等多种题型
- **智能组卷**：按知识点、难度、题型比例自动生成试卷
- **自动阅卷**：客观题自动评分，主观题教师手动批阅
- **线下测评**：支持试卷下载打印，线下考试后录入成绩

### 📊 学情分析
- **成绩统计**：班级/年级/个人多维度成绩分析
- **知识点掌握度**：基于考试数据自动计算各知识点掌握程度
- **趋势分析**：学生成绩变化趋势追踪
- **高低分组区分度**：27% 高低组题目区分度分析

### ⚠️ 预警系统
- **成绩下滑预警**：检测学生成绩持续下降趋势
- **活跃度预警**：检测学生学习活跃度异常
- **低于平均预警**：检测学生成绩低于班级平均水平

### 📋 学情档案
- 综合学生学术档案，涵盖成绩、知识点掌握、行为表现
- 雷达图可视化展示学生综合能力
- 班级学生画像对比分析

### 📄 报告与证书
- **测评报告**：自动生成考试分析报告
- **学习报告**：阶段性学习情况总结
- **证书管理**：竞赛证书、荣誉证书录入与展示

### 🏫 多租户管理
- 支持多学校、多租户数据隔离
- 学校、年级、班级、学生层级化管理
- RBAC 角色权限控制（管理员/教师/家长/学生）

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.3.6 | 应用框架 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| Sa-Token | 1.37.0 | 认证鉴权 |
| PostgreSQL | 16 | 关系型数据库 |
| Redis | 7 | 缓存 |
| SpringDoc | 2.3.0 | API 文档 (Swagger) |
| Hutool | 5.8.25 | 工具库 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.13 | 前端框架 |
| TypeScript | 5.7 | 编程语言 |
| Vite | 6.0 | 构建工具 |
| Element Plus | 2.9.1 | UI 组件库 |
| Pinia | 2.3.0 | 状态管理 |
| ECharts | 5.5.1 | 数据可视化 |
| Axios | 1.7.9 | HTTP 客户端 |

## 项目结构

```
├── learning-analytics/                # 后端 (Spring Boot)
│   ├── learning-common/               # 通用模块 (统一响应、异常处理、常量)
│   ├── learning-model/                # 数据模型 (Entity、Mapper、DTO、VO)
│   ├── learning-service/              # 业务逻辑 (Service 层)
│   ├── learning-admin/                # 管理后台 (Controller、启动类、配置)
│   │   └── src/main/resources/
│   │       ├── application.yml        # 默认配置 (本地开发)
│   │       └── application-docker.yml # Docker 环境配置
│   ├── doc/sql/                       # 数据库脚本
│   │   ├── init.sql                   # DDL 建表语句 (30 张表)
│   │   └── init-data.sql              # 演示数据
│   ├── Dockerfile                     # 后端多阶段构建
│   └── pom.xml                        # Maven 父 POM
│
├── learning-analytics-web/            # 前端 (Vue 3)
│   ├── src/
│   │   ├── api/                       # API 接口封装
│   │   ├── views/                     # 页面组件
│   │   │   ├── admin/                 # 管理员页面 (8 个)
│   │   │   ├── teacher/               # 教师页面 (9 个)
│   │   │   ├── parent/                # 家长页面 (3 个)
│   │   │   ├── student/               # 学生页面 (6 个)
│   │   │   └── dashboard/             # 数据大屏 (3 个)
│   │   ├── stores/                    # Pinia 状态管理
│   │   ├── router/                    # 路由配置
│   │   └── layouts/                   # 布局组件
│   ├── Dockerfile                     # 前端多阶段构建
│   ├── nginx.conf                     # Nginx 配置 (反向代理)
│   ├── vite.config.ts                 # Vite 配置
│   └── package.json
│
├── docker-compose.yml                 # 一键编排 (全部 4 个服务)
├── deploy.sh                          # 一键部署脚本
└── doc/                               # 设计文档
    ├── 学情数据统计分析平台_需求规格说明书.docx
    ├── 学情数据统计分析平台_系统架构设计文档.docx
    ├── 学情数据统计分析平台_数据库设计文档.docx
    ├── 学情数据统计分析平台_API接口设计文档.docx
    └── 学情数据统计分析平台_前端页面原型设计文档.docx
```

## Docker 一键部署（推荐）

只需安装 [Docker](https://docs.docker.com/get-docker/) 和 [Docker Compose](https://docs.docker.com/compose/install/)，无需安装 JDK、Maven、Node.js 等任何依赖。

### 环境要求

| 依赖 | 要求 |
|------|------|
| Docker | 20.10+ |
| Docker Compose | V2+ |

### 一键启动

```bash
# 1. 克隆项目
git clone https://github.com/polikm/learning-analytics.git
cd learning-analytics

# 2. 一键构建并启动（首次构建约 5-10 分钟）
./deploy.sh start

# 3. 导入演示数据（可选）
./deploy.sh init-data
```

启动成功后：
- **前端访问**：http://localhost
- **后端 API**：http://localhost:8080
- **API 文档**：http://localhost/swagger-ui/

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | admin123 |
| 教师 | teacher01 | teacher123 |
| 家长 | parent01 | parent123 |
| 学生 | student01 | student123 |

### 部署管理命令

```bash
./deploy.sh start       # 一键构建并启动
./deploy.sh stop        # 停止所有服务
./deploy.sh restart     # 重启所有服务
./deploy.sh logs        # 查看实时日志
./deploy.sh status      # 查看服务状态
./deploy.sh init-data   # 导入演示数据
./deploy.sh clean       # 清理所有容器、镜像和数据卷
```

### Docker 服务架构

```
                    ┌─────────────────────────────────────────┐
                    │           Docker Compose 网络             │
                    │                                         │
  :80               │  ┌──────────┐       ┌──────────────┐   │
 ─────────────────▶│  │  Nginx   │──API──▶│ Spring Boot  │   │
  前端 (Vue)        │  │ (前端)   │       │   (后端)      │   │
                    │  └──────────┘       └──────┬───────┘   │
                    │                            │           │
  :8080             │                     ┌──────┴───────┐   │
 ─────────────────▶│                     │  PostgreSQL  │   │
  后端 API          │                     │   Redis      │   │
                    │                     └──────────────┘   │
                    └─────────────────────────────────────────┘
```

## 本地开发部署

如果你需要本地开发调试，可以按以下步骤操作：

### 环境要求

| 依赖 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Docker & Docker Compose | 最新稳定版 |

### 1. 启动基础设施

```bash
cd learning-analytics
docker-compose up -d postgres redis
```

### 2. 启动后端

```bash
cd learning-analytics
mvn clean install -DskipTests
cd learning-admin
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd learning-analytics-web
npm install
npm run dev
```

前端开发服务器：http://localhost:3000（自动代理 API 到 8080）

## 配置说明

### 后端配置

配置文件位于 `learning-analytics/learning-admin/src/main/resources/`：

| 文件 | 用途 |
|------|------|
| `application.yml` | 默认配置（本地开发，连接 localhost） |
| `application-docker.yml` | Docker 环境（连接 Docker 服务名） |

主要配置项：

```yaml
# 数据库连接
spring.datasource.url: jdbc:postgresql://localhost:5432/learning_analytics
spring.datasource.username: postgres
spring.datasource.password: postgres123

# Redis 连接
spring.data.redis.host: localhost
spring.data.redis.port: 6379
spring.data.redis.password: redis123

# Token 有效期
sa-token.timeout: 86400        # 24 小时
sa-token.active-timeout: 1800  # 30 分钟无操作过期
```

## License

MIT
