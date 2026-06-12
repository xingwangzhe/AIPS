# AIPS — AI 线上购药系统

[![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-blue.svg)](LICENSE)

**A**I **P**harmacy **S**ystem — 深度融合大语言模型的智能医药服务平台。提供 AI 药师咨询、个性化用药推荐、处方辅助审核、在线购药配送等一站式健康服务。

## 技术栈

### 后端

| 技术 | 版本 |
|------|------|
| Java | 25 |
| Spring Boot | 4.1.0 |
| Gradle (Groovy DSL) | 9.x |
| Hibernate | 7.4 |
| PostgreSQL | 18.4 |
| Spring Security + JWT | — |
| Spring AI (DeepSeek V4) | — |

### 前端

| 技术 | 版本 |
|------|------|
| Vue 3 | 3.5 |
| Vite | 8.0 |
| Pinia | 3.0 |
| Vue Router | 5.1 |
| Element Plus | 2.14 |
| Tailwind CSS | 4.3 |
| oxlint / oxfmt | — |
| Vitest | 4.1 |

## 快速开始

### 环境要求

- Java 25+
- Node.js 24+ / Bun 1.3+
- PostgreSQL 16+
- IntelliJ IDEA 2025+（推荐）

### 1. 克隆仓库

```bash
git clone git@github.com:xingwangzhe/AIPS.git
cd AIPS
```

### 2. 配置环境变量

```bash
cp .env.example .env          # 编辑填入真实数据库/LLM 密钥
cp frontend/.env.example frontend/.env
```

### 3. 初始化数据库

```bash
sudo -u postgres psql << SQL
CREATE USER aips WITH PASSWORD 'aips123';
CREATE DATABASE aips OWNER aips;
\c aips
GRANT ALL ON SCHEMA public TO aips;
SQL
```

首次启动后端时 JPA 会自动建表（18 张）并插入种子数据（14 分类 + 8 药品 + 3 药师 + 2 横幅）。

### 4. 启动

#### IntelliJ IDEA 一键启动（推荐）

打开项目后，工具栏 Run Configurations 下拉选择：

| 配置 | 说明 |
|------|------|
| **Full Stack (Backend + Frontend)** | 前后端同时启动 |
| Backend (bootRun) | 仅后端（端口 8080） |
| Frontend (bun dev) | 仅前端（端口 5173） |

#### 命令行启动

```bash
# 后端
./gradlew bootRun        # → http://localhost:8080

# 前端（新终端）
cd frontend
bun install
bun dev                  # → http://localhost:5173
```

### 5. 登录

访问 `http://localhost:5173/login`，输入手机号，验证码固定为 `123456`（开发模式）。

## 项目结构

```
AIPS/
├── src/                          # 后端 Spring Boot
│   └── main/java/com/needhelp/aips/
│       ├── common/               # 通用组件（BaseEntity, ApiResponse, JwtUtil…）
│       ├── entity/               # JPA 实体（18 张表）
│       ├── repository/           # Spring Data JPA
│       ├── service/              # 业务服务（drug/consult/prescription/order/user）
│       ├── infrastructure/       # 基础服务（auth/ai/file/payment/message）
│       ├── controller/           # REST 控制器（14 个 API）
│       └── dto/                  # 请求/响应 DTO
├── frontend/                     # 前端 Vue 3 + Vite
│   └── src/
│       ├── api/                  # Axios 封装 + 5 个 API 模块
│       ├── stores/               # Pinia（auth/cart/drug）
│       ├── views/                # 10 个页面组件
│       ├── components/           # 通用组件（AppHeader, BottomNav, DrugCard…）
│       ├── router/               # Vue Router + 登录守卫
│       └── utils/                # 格式化工具
├── docs/                         # 设计文档（需求/数据库/API/架构）
├── .idea/runConfigurations/      # IDEA 启动配置（Backend / Frontend / Full Stack）
├── .env.example                  # 后端环境变量模板
└── LICENSE                       # AGPL-3.0
```

## API 端点总览

| 模块 | 方法 | 路径 | 认证 |
|------|------|------|------|
| 药品 | GET | `/api/v1/drug/categories` | ❌ |
| 药品 | GET | `/api/v1/drug/search` | ❌ |
| 药品 | GET | `/api/v1/drug/{id}` | ❌ |
| 用户 | POST | `/api/v1/user/login` | ❌ |
| 用户 | GET | `/api/v1/user/health` | ✅ |
| 用户 | POST | `/api/v1/user/reminder` | ✅ |
| 咨询 | POST | `/api/v1/consult/session` | ✅ |
| 咨询 | POST | `/api/v1/consult/session/{id}/message` | ✅ |
| 咨询 | POST | `/api/v1/consult/session/{id}/transfer` | ✅ |
| 处方 | POST | `/api/v1/prescription/upload` | ✅ |
| 处方 | GET | `/api/v1/prescription/{id}` | ✅ |
| 订单 | POST | `/api/v1/cart/item` | ✅ |
| 订单 | POST | `/api/v1/order` | ✅ |
| 订单 | GET | `/api/v1/order/list` | ✅ |

## 常见命令

```bash
# === 后端 ===
./gradlew build              # 编译 + 测试
./gradlew bootRun            # 启动（端口 8080）
./gradlew test               # 运行测试
./gradlew clean build        # 清理重建

# === 前端 ===
cd frontend
bun dev                      # 启动 dev server（端口 5173，热更新）
bun run build                # 生产构建
bun vitest run               # 运行测试
bunx oxlint .                # Lint 检查
bunx oxfmt --check .         # 格式检查
bunx oxfmt --write .         # 自动格式化
```

## License

[GNU Affero General Public License v3.0](LICENSE) — Copyright © 2025–2026 AIPS Contributors
