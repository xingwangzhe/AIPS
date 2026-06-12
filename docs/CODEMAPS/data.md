<!-- Generated: 2026-06-12 | Files scanned: 8 | Token estimate: ~150 -->

# AIPS — Data Layer

## Database
- **Type:** PostgreSQL
- **Driver:** `org.postgresql:postgresql` (runtime)
- **ORM:** Hibernate via Spring Data JPA

## Entities
*None defined yet.*

## Migrations
*No migration tool configured.* Consider adding Flyway or Liquibase.

## Configuration Needed
```properties
# application.properties — add:
spring.datasource.url=jdbc:postgresql://localhost:5432/aips
spring.datasource.username=
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=validate
```
