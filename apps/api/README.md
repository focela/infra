# Focela Platform — Backend API

Spring Boot 3 backend for the Focela platform. Multi-module Maven project derived from the [yudao](https://github.com/YunaiV/ruoyi-vue-pro) framework and rebranded for Focela ([REFACTOR_PLAN_LEVEL_B.md](docs/REFACTOR_PLAN_LEVEL_B.md)).

## Tech stack

| Layer | Choice | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.5.9 |
| Build | Maven | 3.9+ |
| ORM | MyBatis-Plus | 3.5.x |
| Database | PostgreSQL | 16 |
| Cache / Broker | Redis | 7 |
| Auth | Spring Security + JWT | 6.x |
| API docs | SpringDoc / OpenAPI | 2.x |
| Test | JUnit 5, Mockito, H2 (in-memory) | — |

## Module layout

```
apps/api/
├── focela-dependencies/                 # BOM — version management
├── focela-framework/                    # Reusable Spring Boot starters
│   ├── focela-common/                   # Cross-module shared types
│   └── focela-spring-boot-starter-*/    # 15 starters (web, security, mybatis, redis, mq, …)
├── focela-module-infra/                 # Infrastructure module (file, job, logger, config, …)
├── focela-module-system/                # System module (auth, user, role, mail, sms, notify, …)
└── focela-server/                       # Bootable Spring Boot application
```

Maven coordinates: `com.focela.platform:<artifact>:1.0.0-SNAPSHOT`
Base Java package: `com.focela.platform.*`

## Package convention (per module)

```
com.focela.platform.module.<bounded-context>/
├── api/                  # Cross-module RPC contracts (XxxApi + LocalXxxApi)
├── controller/
│   ├── admin/            # Admin Web API (/admin-api/...)
│   └── app/              # Mobile/App API (/app-api/...)
│       └── <feature>/dto/  # Request/Response DTOs
├── converter/            # MapStruct converters (XxxConverter)
├── enums/                # Enum types
├── framework/            # Module-specific framework code
├── repository/
│   ├── entity/           # MyBatis entities (XxxEntity extends BaseEntity)
│   ├── mapper/           # MyBatis Mapper interfaces (XxxMapper)
│   └── redis/            # Redis DAOs
├── service/              # Business services (XxxService + DefaultXxxService)
└── utils/                # Module utilities
```

## Naming conventions

| Pattern | Example |
|---|---|
| Entity (DB) | `UserEntity`, `DepartmentEntity` extends `BaseEntity` |
| Mapper (MyBatis) | `UserMapper extends BaseMapperX<UserEntity>` |
| Service interface | `UserService` |
| Service impl | `DefaultUserService implements UserService` |
| Cross-module API | `UserApi` + `LocalUserApi` |
| Request DTO | `UserSaveRequest`, `UserPageRequest`, `UserListRequest` |
| Response DTO | `UserResponse`, `UserSimpleResponse` |
| Converter | `UserConverter` (MapStruct) |
| Excel DTO | `UserImportExcelDto` |
| Configuration class | `FocelaXxxAutoConfiguration` |
| Spring contract impl | Behavior-based name — e.g. `JsonAccessDeniedHandler` |

Config prefix: all custom properties use `focela.*` (e.g. `focela.security`, `focela.tenant`, `focela.web`).

## Local development

### Prerequisites

- Java 17 (Homebrew: `brew install openjdk@17`)
- Maven 3.9+ (or use the bundled `./mvnw` wrapper — recommended)
- Docker (for PostgreSQL + Redis containers)
- An IDE with Lombok plugin enabled

### Start dependencies

```bash
docker run -d --name uat-postgres \
  -e POSTGRES_USER=root -e POSTGRES_PASSWORD=123456 \
  -e POSTGRES_DB=ruoyi-vue-pro \
  -p 5432:5432 postgres:16.13

docker run -d --name uat-redis \
  -p 6379:6379 redis:7.4.8 \
  redis-server --requirepass 123456
```

PostgreSQL schema must be provisioned manually (no migration tool yet — see [Open gaps](#open-gaps)). Cloned-from-prod dumps work fine.

### Run the API

```bash
# free port if already in use
kill -9 $(lsof -t -i:48080) 2>/dev/null

cd apps/api
./mvnw clean install -DskipTests
./mvnw spring-boot:run -pl focela-server
```

API serves on `http://127.0.0.1:48080`. Admin endpoints under `/admin-api/...`, mobile under `/app-api/...`. Both require authentication; expect `401` until you log in.

### Profiles

| Profile | Purpose |
|---|---|
| `local` (default) | Local dev with PostgreSQL + Redis on 127.0.0.1 |
| `dev` | Shared dev environment |
| `unit-test` | Auto-applied during `./mvnw test` — uses H2 in-memory |

`application-local.yaml` is gitignored — keep your local secrets out of the repo.

## Build & test

```bash
./mvnw -f pom.xml clean test-compile -T 1C    # Quick syntax check
./mvnw -f pom.xml clean install -DskipTests   # Install all modules
./mvnw -f pom.xml test                        # Full test suite (uses H2)
```

Current test baseline: **457 run, 456 pass, 1 timezone-dependent failure** (`DefaultQiniuSmsClientTest.testParseSmsReceiveStatus` — see [open gaps](#open-gaps)).

## Architecture decisions

| Decision | Why |
|---|---|
| Monorepo `apps/api/` | Future-proof for additional apps (`apps/web/`, `apps/worker/`) |
| Layered (controller → service → repository) | Inherited from yudao baseline; pragmatic, not feature-first |
| `*Api` interface + `Local*Api` impl | Cross-module contract. `Local*` prefix signals same-JVM implementation; a future `Remote*Api` can sit alongside when modules move to RPC. |
| `*Service` + `Default*Service` | Spring convention (`DefaultListableBeanFactory`); avoids `*Impl` smell while keeping interface for AOP/mocking |
| MyBatis-Plus over JPA | Yudao baseline; better fit for legacy PostgreSQL schema |
| `focela.*` config prefix | Single namespace for application properties |

## Open gaps

| Gap | Impact | Tracker |
|---|---|---|
| No Flyway/Liquibase schema migration | Schema drift between H2 (test) and PostgreSQL (prod); manual provisioning | `docs/REFACTOR_PLAN_LEVEL_B.md` (Mức C+) |
| Chinese comments throughout | Onboarding friction for non-CN devs | — |
| `QiniuSmsClientTest` timezone hardcoded | Test fails outside Asia/Shanghai (UTC+8) | Tracked separately |
| No CI/CD | Manual gate only | Postponed |

## Useful URLs

- Swagger UI: `http://127.0.0.1:48080/doc.html` (when running)
- OpenAPI JSON: `http://127.0.0.1:48080/v3/api-docs`
- Druid SQL monitor: `http://127.0.0.1:48080/druid/`
- Actuator: `http://127.0.0.1:48080/actuator/info`

## History

The codebase was rebranded from yudao (Chinese open-source project) to Focela in a series of refactor phases on `feature/SKF-1`:

- **Phase A** — Rebrand Maven coordinates, Java base package, class names, config prefix (`yudao.*` → `focela.*`)
- **Phase B** — Standardize naming: `dal/dataobject/` → `repository/entity/`, `vo/` → `dto/`, `*DO` → `*Entity`, `*ReqVO/*RespVO` → `*Request/*Response`
- **Phase C** — International naming: `dept` → `department`, `dict` → `dictionary`, `*ServiceImpl` → `Default*Service`, etc.
- Codegen module removed (FE migrating to React)

See `git log --oneline main..feature/SKF-1` for full commit history.
