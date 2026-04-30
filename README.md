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
│   ├── doc/sql/                       # 数据库脚本
│   │   ├── init.sql                   # DDL 建表语句 (30 张表)
│   │   └── init-data.sql              # 演示数据
│   ├── docker-compose.yml             # PostgreSQL + Redis 容器编排
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
│   ├── vite.config.ts                 # Vite 配置
│   └── package.json
│
└── doc/                               # 设计文档
    ├── 学情数据统计分析平台_需求规格说明书.docx
    ├── 学情数据统计分析平台_系统架构设计文档.docx
    ├── 学情数据统计分析平台_数据库设计文档.docx
    ├── 学情数据统计分析平台_API接口设计文档.docx
    └── 学情数据统计分析平台_前端页面原型设计文档.docx
```

## 环境要求

| 依赖 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Docker & Docker Compose | 最新稳定版 |
| PostgreSQL | 16 (通过 Docker 部署) |
| Redis | 7 (通过 Docker 部署) |

## 快速部署

### 1. 克隆项目

```bash
git clone https://github.com/polikm/learning-analytics.git
cd learning-analytics
```

### 2. 启动基础设施 (PostgreSQL + Redis)

```bash
cd learning-analytics
docker-compose up -d
```

这将启动：
- **PostgreSQL 16**：端口 `5432`，用户名 `postgres`，密码 `postgres123`，数据库 `learning_analytics`
- **Redis 7**：端口 `6379`，密码 `redis123`

数据库表结构会在 PostgreSQL 首次启动时自动创建（通过 `init.sql`）。

### 3. 导入演示数据（可选）

```bash
docker exec -i learning-postgres psql -U postgres -d learning_analytics < doc/sql/init-data.sql
```

演示数据包含：
- 1 个租户、1 所学校、3 个年级、6 个班级
- 10 个用户（管理员、教师、家长、学生）
- 4 个学科、11 个知识点、10 道试题、1 套试卷
- 1 场考试、5 条考试记录、50 条答题详情
- 3 张证书、2 个字典类型

### 4. 启动后端

```bash
cd learning-analytics
mvn clean install -DskipTests
cd learning-admin
mvn spring-boot:run
```

后端启动后：
- 服务地址：`http://localhost:8080`
- API 文档 (Swagger)：`http://localhost:8080/swagger-ui.html`

### 5. 启动前端

```bash
cd learning-analytics-web
npm install
npm run dev
```

前端启动后访问：`http://localhost:3000`

### 6. 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | admin123 |
| 教师 | teacher01 | teacher123 |
| 家长 | parent01 | parent123 |
| 学生 | student01 | student123 |

## 配置说明

后端配置文件位于 `learning-analytics/learning-admin/src/main/resources/application.yml`，主要配置项：

```yaml
# 数据库连接
spring.datasource.url: jdbc:postgresql://localhost:5432/learning_analytics
spring.datasource.username: postgres
spring.datasource.password: postgres123

# Redis 连接
spring.data.redis.host: localhost
spring.data.redis.port: 6379
spring.data.redis.password: redis123

# 服务端口
server.port: 8080

# Token 有效期 (秒)
sa-token.timeout: 86400        # 24 小时
sa-token.active-timeout: 1800  # 30 分钟无操作过期
```

前端开发代理配置位于 `learning-analytics-web/vite.config.ts`，默认将 `/api` 请求代理到 `http://localhost:8080`。

## 构建部署

### 后端打包

```bash
cd learning-analytics
mvn clean package -DskipTests
# 生成的 JAR 位于 learning-admin/target/learning-admin-1.0.0-SNAPSHOT.jar
java -jar learning-admin/target/learning-admin-1.0.0-SNAPSHOT.jar
```

### 前端打包

```bash
cd learning-analytics-web
npm run build
# 生成的静态文件位于 dist/ 目录，可部署到 Nginx 等 Web 服务器
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /path/to/learning-analytics-web/dist;
    index index.html;

    # 前端路由 history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 系统架构

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Vue 3 前端  │────▶│  Spring Boot  │────▶│  PostgreSQL  │
│  (Element+)  │ API │   后端服务    │ SQL │   数据库     │
└──────────────┘     └──────┬───────┘     └──────────────┘
                            │
                     ┌──────┴───────┐
                     │    Redis     │
                     │   缓存/会话   │
                     └──────────────┘
```

## License

MIT
