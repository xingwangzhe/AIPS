# AIPS — AI 线上购药系统

[![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-blue.svg)](LICENSE)

**A**I **P**harmacy **S**ystem — 深度融合大语言模型的智能医药服务平台。提供 AI 药师咨询、个性化用药推荐、处方辅助审核、在线购药配送等一站式健康服务。

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | OpenJDK |
| Spring Boot | 3.4.5 | Spring Framework 6.x |
| Gradle (Groovy DSL) | 9.x | |
| Hibernate | 6.x | JPA 实现 |
| PostgreSQL | 18.4 | 主数据库 |
| H2 | — | 测试数据库 |
| Spring Security + JWT | — | 无状态认证 |
| LangChain4j | 1.16.2 | LLM 框架 |
| DeepSeek V4 | — | AI 模型（OpenAI 兼容协议） |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue 3 | 3.5 | Composition API + `<script setup>` |
| Vite | 8.0 | 构建工具 |
| Pinia | 3.0 | 状态管理 |
| Vue Router | 5.1 | 路由 |
| Element Plus | 2.14 | UI 组件库 |
| Tailwind CSS | 4.3 | 原子化 CSS |
| oxlint / oxfmt | — | Lint / 格式化 |
| Vitest | 4.1 | 单元测试 |

## 快速开始

### 环境要求

- JDK 21+
- Node.js 24+ / Bun 1.3+
- PostgreSQL 16+

### 1. 克隆仓库

```bash
git clone git@github.com:xingwangzhe/AIPS.git
cd AIPS
```

### 2. 配置环境变量

```bash
cp .env.example .env          # 填入数据库密码、LLM API Key
cp frontend/.env.example frontend/.env
```

### 3. 初始化数据库

```bash
sudo -u postgres psql << SQL
CREATE USER aips WITH PASSWORD 'aips123';
CREATE DATABASE aips OWNER aips;
GRANT ALL ON SCHEMA public TO aips;
SQL
```

首次启动时 JPA 自动建表（18 张）并插入种子数据（14 分类 + 8 药品 + 3 药师 + 2 横幅）。

### 4. 启动

#### IDEA 一键启动（推荐）

Run Configurations → **Full Stack (Backend + Frontend)**

| 配置 | 说明 |
|------|------|
| Full Stack | 前后端同时启动 |
| Backend (bootRun) | 后端 :8080 |
| Frontend (bun dev) | 前端 :5173 |

#### 命令行

```bash
# 后端（需 JDK 21）
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew bootRun        # → http://localhost:8080

# 前端（新终端）
cd frontend && bun dev   # → http://localhost:5173
```

### 5. 登录

`http://localhost:5173/login`，验证码 `123456`（开发模式）。

## 项目结构

```
AIPS/
├── src/main/java/com/needhelp/aips/
│   ├── common/               # BaseEntity, ApiResponse, JwtUtil, SecurityConfig…
│   ├── entity/               # JPA 实体 (18 tables)
│   ├── repository/           # Spring Data JPA
│   ├── service/              # drug / consult / prescription / order / user
│   ├── infrastructure/       # auth / file / payment / message
│   ├── controller/           # REST API (14 endpoints)
│   └── dto/                  # Request / Response DTOs
├── frontend/src/
│   ├── api/                  # Axios + 5 个 API 模块
│   ├── stores/               # Pinia (auth / cart / drug)
│   ├── views/                # 10 个页面
│   ├── components/           # AppHeader, BottomNav, DrugCard, ChatMessage…
│   ├── router/               # Vue Router + auth guard
│   └── utils/                # formatPrice, formatTime, maskPhone…
├── docs/                     # 需求 / 数据库 / API / 架构设计文档
├── .idea/runConfigurations/  # IDEA 启动配置
└── .env.example              # 环境变量模板
```

## RAG 检索增强

AI 药师咨询的 RAG 流程：

```
用户"头痛发烧" → MedicineRepository.searchByKeyword() → DB 匹配 5 条药品
  → 拼入 Prompt 作为上下文（名称/价格/库存/适应症）
  → LangChain4j ChatModel.chat() → DeepSeek V4
  → 返回结构化 JSON（content / symptomAnalysis / medicineAdvice / warnings / riskLevel）
```

未配置 LLM API Key 时自动降级为关键词模拟回复。

## API 端点

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
# === 后端 (JDK 21) ===
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew bootRun            # 启动 :8080
./gradlew test               # 5 tests
./gradlew build              # 编译 + 测试

# === 前端 ===
cd frontend
bun dev                      # :5173
bun run build                # 生产构建
bun vitest run               # 测试
bunx oxlint . && bunx oxfmt --check .   # 质量检查
```

## License

[GNU Affero General Public License v3.0](LICENSE) — Copyright © 2025–2026 AIPS Contributors
