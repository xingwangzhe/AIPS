<!-- Generated: 2026-06-12 | Files scanned: 8 | Token estimate: ~300 -->

# AIPS — Architecture Overview

## Project Type
Spring Boot monolith (single-module Gradle project)

## Tech Stack
- **Language:** Java 25 (toolchain)
- **Framework:** Spring Boot 4.1.0
- **Build:** Gradle (Groovy DSL)
- **Database:** PostgreSQL (via spring-data-jpa)
- **Web:** Spring WebMVC
- **Validation:** Jakarta Bean Validation (spring-boot-starter-validation)
- **Monitoring:** Spring Boot Actuator
- **Boilerplate:** Lombok

## Entry Point
`com.needhelp.aips.AipsApplication` — standard `@SpringBootApplication` main class

## Package Structure
```
com.needhelp.aips
  └── AipsApplication.java        # Application entry point
```

## Data Flow
```
HTTP Request → DispatcherServlet → Controller (TBD) → Service (TBD) → JPA Repository (TBD) → PostgreSQL
```

## Current State
**Skeleton project** — no controllers, services, repositories, or entities defined yet.
- `application.properties`: only `spring.application.name=AIPS` configured
- Test: single `contextLoads()` smoke test

## External Dependencies
| Service | Purpose |
|---------|---------|
| PostgreSQL | Primary data store (runtime) |
| Maven Central | Dependency repository |
