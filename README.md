# AIPS — AI 线上购药系统

[![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-blue.svg)](LICENSE)

**A**I **P**harmacy **S**ystem — 深度融合大语言模型的智能医药服务平台。提供 AI 药师咨询、个性化用药推荐、处方辅助审核、在线购药配送等一站式健康服务。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | OpenJDK |
| Spring Boot | 3.4.5 | Spring Framework 6.x |
| Gradle (Groovy DSL) | 9.x | |
| Hibernate | 6.x | JPA |
| PostgreSQL | 18.4 | 主数据库 + pgvector 向量扩展 |
| pgvector | — | 语义向量检索（1536 维） |
| H2 | — | 测试数据库 |
| Spring Security + JWT | — | 无状态认证 |
| LangChain4j | 1.16.2 | LLM 框架 |
| DeepSeek V4 | — | Chat + Embedding（OpenAI 兼容协议） |
| Vue 3 | 3.5 | Composition API + `<script setup>` |
| Vite | 8.0 | 构建工具 |
| Pinia / Vue Router | 3.0 / 5.1 | 状态管理 / 路由 |
| Element Plus | 2.14 | UI 组件库 |
| Tailwind CSS | 4.3 | 原子化 CSS |
| oxlint / oxfmt | — | Lint / 格式化 |
| Vitest | 4.1 | 单元测试 |

## 快速开始

### 环境要求

- JDK 21+
- Node.js 24+ / Bun 1.3+
- PostgreSQL 18+ + pgvector 扩展

### 1. 克隆仓库

```bash
git clone git@github.com:xingwangzhe/AIPS.git
cd AIPS
```

### 2. 安装 pgvector

```bash
sudo apt-get install -y postgresql-18-pgvector
```

### 3. 配置环境变量

```bash
cp .env.example .env          # 填入数据库密码、LLM API Key
cp frontend/.env.example frontend/.env
```

### 4. 初始化数据库

```bash
sudo -u postgres psql << SQL
CREATE USER aips WITH PASSWORD 'aips123';
CREATE DATABASE aips OWNER aips;
GRANT ALL ON SCHEMA public TO aips;
\c aips
CREATE EXTENSION vector;
SQL
```

首次启动时 JPA 自动建表（18 张）并插入种子数据（14 分类 + 8 药品 + 3 药师 + 2 横幅）。药品向量索引（1536 维）在应用启动时自动通过 DeepSeek Embedding API 生成。

### 5. 启动

```bash
# 后端（JDK 21）
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew bootRun              # → http://localhost:8080

# 前端（新终端）
cd frontend && bun dev         # → http://localhost:5173
```

IDEA 可直接用 Run Configurations → **Full Stack** 一键启动前后端。

### 6. 登录

`http://localhost:5173/login`，验证码 `123456`（开发模式）。

## RAG 检索增强

AI 药师咨询使用 **pgvector + DeepSeek Embedding API** 实现语义向量检索：

```
用户"胃疼"
  → DeepSeek Embedding API → 1536 维向量
  → pgvector <=> 余弦相似度排序 → top 5 语义匹配药品
  → 药品上下文（名称/价格/库存/适应症）+ 对话历史 → 注入 ChatRequest
  → LangChain4j ChatModel.chat() → DeepSeek V4
  → 结构化 JSON 回复 + 药品推荐卡片
```

- 向量检索为**主搜索路径**，keyword LIKE 仅作降级后备
- 匹配到的药品以**可点击卡片**形式展示（名称/价格/库存/适应症），点击跳转详情页
- 对话历史（最近 10 条）作为上下文注入，支持多轮追问
- LLM / Embedding API 异常时自动降级为数据库搜索 + 症状模板回复

## 项目结构

```
AIPS/
├── src/main/java/com/needhelp/aips/
│   ├── common/               # BaseEntity, ApiResponse, JwtUtil, SecurityConfig…
│   ├── entity/               # JPA 实体 (18 tables)
│   ├── repository/           # Spring Data JPA + MedicineVectorRepository (pgvector)
│   ├── service/              # drug / consult / prescription / order / user
│   ├── infrastructure/       # auth / EmbeddingService / file / payment / message
│   ├── controller/           # REST API (14 endpoints)
│   └── dto/                  # Request / Response DTOs
├── frontend/src/
│   ├── api/                  # Axios + 5 API 模块
│   ├── stores/               # Pinia (auth / cart / drug)
│   ├── views/                # 10 页面
│   ├── components/           # AppHeader, BottomNav, DrugCard, ChatMessage…
│   ├── router/               # Vue Router + auth guard
│   └── utils/                # formatPrice, maskPhone…
├── docs/                     # 需求 / 数据库 / API / 架构设计文档
├── .idea/runConfigurations/  # IDEA 启动配置
└── .env.example              # 环境变量模板
```

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
./gradlew bootRun            # :8080
./gradlew test               # 5 tests
./gradlew build              # 编译 + 测试

# === 前端 ===
cd frontend
bun dev                      # :5173
bun run build                # 生产构建
bun vitest run               # 测试
bunx oxlint . && bunx oxfmt --check .
```

## License

[GNU Affero General Public License v3.0](LICENSE) — Copyright © 2025–2026 AIPS Contributors
