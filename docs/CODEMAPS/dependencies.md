<!-- Generated: 2026-06-12 | Files scanned: 8 | Token estimate: ~250 -->

# AIPS — Dependencies

## Runtime
| Dependency | Version | Purpose |
|------------|---------|---------|
| spring-boot-starter-webmvc | 4.1.0 | REST/Web controllers, embedded Tomcat |
| spring-boot-starter-data-jpa | 4.1.0 | JPA repositories, Hibernate ORM |
| spring-boot-starter-validation | 4.1.0 | Jakarta Bean Validation |
| spring-boot-starter-actuator | 4.1.0 | Health checks, metrics, info endpoints |
| postgresql | (managed) | PostgreSQL JDBC driver |
| lombok | (managed) | Boilerplate reduction (compileOnly) |

## Development Only
| Dependency | Purpose |
|------------|---------|
| spring-boot-devtools | Hot reload, automatic restart |

## Test
| Dependency | Purpose |
|------------|---------|
| spring-boot-starter-test | Base test support + JUnit 5 |
| spring-boot-starter-actuator-test | Actuator endpoint testing |
| spring-boot-starter-data-jpa-test | JPA slice testing |
| spring-boot-starter-validation-test | Validation testing |
| spring-boot-starter-webmvc-test | Web MVC slice testing |
| junit-platform-launcher | JUnit Platform test execution |

## Build
| Tool | Version |
|------|---------|
| Gradle | (wrapper) |
| Spring Dependency Management plugin | 1.1.7 |
| Java toolchain | 25 |

## External Services
| Service | Status |
|---------|--------|
| PostgreSQL | Configured, not connected |

## Maven Repositories
- Maven Central
