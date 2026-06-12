<!-- Generated: 2026-06-12 | Files scanned: 8 | Token estimate: ~200 -->

# AIPS — Backend Architecture

## Routes
*No routes defined yet* — project is a skeleton with no controllers.

## Layers (planned — based on dependencies)
```
Controller (TBD) → Service (TBD) → JPA Repository (TBD)
```

## Key Files
| File | Purpose | Lines |
|------|---------|-------|
| `src/main/java/com/needhelp/aips/AipsApplication.java` | Spring Boot entry point | 13 |
| `src/main/resources/application.properties` | App config (`spring.application.name=AIPS`) | 1 |

## Middleware Chain (Spring Boot defaults + Actuator)
- Spring Security: *not included*
- Actuator endpoints: enabled via `spring-boot-starter-actuator`

## Validation
Jakarta Bean Validation on classpath — ready for DTO/entity validation.

## Database
- **ORM:** JPA / Hibernate (via spring-boot-starter-data-jpa)
- **Driver:** PostgreSQL (`org.postgresql:postgresql`)
- **Connection config:** *not yet set* (needs `spring.datasource.*` properties)
