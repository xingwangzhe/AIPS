# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Identity

**AIPS** (AI Pharmacy System, 线上购药系统) — an intelligent medical service platform integrating LLM technology for AI pharmacist consultation, personalized medication recommendations, prescription review, and online drug purchasing.

## Build & Test Commands

```bash
# Build the project
./gradlew build

# Run only tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.needhelp.aips.AipsApplicationTests"

# Run a specific test method
./gradlew test --tests "com.needhelp.aips.AipsApplicationTests.contextLoads"

# Boot the application (dev mode with hot reload)
./gradlew bootRun

# Clean build
./gradlew clean build
```

## Architecture

**Stack:** Spring Boot 4.1.0 — Java 25 — Gradle (Groovy DSL) — PostgreSQL — JPA/Hibernate

**System layers** (see `docs/线上购药系统功能模块划分.md`):
```
表现层 (Mobile App / Web Admin)
  → API Gateway
    → 业务服务层 (6 core services):
        Medicine Service, AI Consultation Service, Prescription Service,
        Order Service, User Service, Personalization Service
    → 公共基础服务层 (4 shared services):
        Auth Center, File Center, Payment Center, Message Center
      → 数据层 (PostgreSQL, Redis Cache, Elasticsearch, Object Storage, AI Models)
```

**Current code state:** Skeleton phase — `AipsApplication.java` is the only source file. Build config and dependencies are in place; entity, repository, service, and controller layers are not yet implemented.

## API Conventions

From `docs/线上购药系统接口设计文档.md`:
- Prefix: `/api/v1`
- Auth: Bearer Token (JWT)
- Data format: JSON (`application/json; charset=UTF-8`)
- HTTPS only in production
- Unified response envelope: `{ "code": 200, "message": "success", "data": {}, "timestamp": 1700000000000 }`
- Time format: ISO 8601
- Currency: Decimal, CNY with 2 decimal places

## Database Design

Design docs at `docs/线上购药系统数据库设计.md`. Key conventions:
- Primary keys: `BIGINT` auto-increment, named `id`
- Foreign keys: `{referenced_table}_id` format
- All tables include `create_time` and `update_time` audit columns
- Status fields: `TINYINT` with comments
- Sensitive data (phone, ID card) encrypted at rest
- 7 table groups: user, medicine, cart/order, prescription, AI consultation, health profile, system support

## Design Documents

All in `docs/` (Chinese). The authoritative spec is `线上购药系统需求规格说明书.md`. Codemaps are at `docs/CODEMAPS/` for quick AI context loading.
