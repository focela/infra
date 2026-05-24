# Focela Platform — Backend API

Spring Boot 3 backend for the Focela platform. Multi-module Maven project maintained under Focela platform conventions ([REFACTOR_PLAN_LEVEL_B.md](docs/REFACTOR_PLAN_LEVEL_B.md)).

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
├── focela-infra/                        # Infrastructure module (file, job, logger, config, …)
├── focela-system/                       # System module (auth, user, role, mail, sms, notify, …)
└── focela-server/                       # Bootable Spring Boot application
```

Maven coordinates: `com.focela.platform:<artifact>:1.0.0-SNAPSHOT`
Base Java package: `com.focela.platform.*`

## Package convention (per module)

```
com.focela.platform.<bounded-context>/
├── api/                  # Module-side RPC surface (XxxApi + LocalXxxApi); cross-module
│                         # contracts live in framework/common/api/ as XxxContractApi
├── config/               # Module-specific Spring config and integration code
├── constants/            # Compile-time constants (ErrorCodeConstants, …)
├── controller/
│   ├── admin/            # Admin Web API (/admin-api/...)
│   └── app/              # Mobile/App API (/app-api/...)
│       └── <feature>/    # Request/Response payloads split into request/ and response/
├── converter/            # MapStruct converters (XxxConverter)
├── domain/entity/        # MyBatis entities (XxxEntity extends BaseEntity)
├── enums/                # Enum types only (constants live in constants/)
├── job/                  # Scheduled jobs
├── mq/                   # Message queue producers/consumers
├── repository/
│   ├── mapper/           # MyBatis Mapper interfaces (XxxMapper)
│   └── redis/            # Redis DAOs
└── service/              # Business services (XxxService + DefaultXxxService)
```

## Naming conventions

| Pattern | Example |
|---|---|
| Entity (DB) | `UserEntity`, `DepartmentEntity` extends `BaseEntity` |
| Mapper (MyBatis) | `UserMapper extends BaseMapperX<UserEntity>` |
| Service interface | `UserService` |
| Service impl | `DefaultUserService implements UserService` |
| Cross-module contract (framework/common/api/) | `OperateLogContractApi`, `PermissionContractApi` |
| Module-side API (module/api/) | `UserApi`, `OperateLogApi extends OperateLogContractApi` |
| API impl (same JVM) | `LocalUserApi implements UserApi` |
| HTTP Request DTO (controller) | `UserSaveRequest`, `UserPageRequest`, `UserListRequest` |
| HTTP Response DTO (controller) | `UserResponse`, `UserSimpleResponse` |
| Cross-module RPC Request DTO | `OperateLogCreateRpcRequest`, `MailSendSingleToUserRpcRequest` |
| Cross-module RPC Response DTO | `UserRpcResponse`, `OperateLogRpcResponse` |
| Converter | `UserConverter` (MapStruct) |
| Excel row | `UserImportExcelRow` |
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
  -e POSTGRES_DB=focela-platform \
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

Current test baseline: **580 run, 0 failures, 0 errors, 19 skipped** — full suite green on any host timezone.

## Architecture decisions

| Decision | Why |
|---|---|
| Monorepo `apps/api/` | Future-proof for additional apps (`apps/web/`, `apps/worker/`) |
| Layered (controller → service → repository) | Inherited from the legacy upstream baseline; pragmatic, not feature-first |
| `*ContractApi` (in framework/common/api/) + `*Api` (module-side) + `Local*Api` impl | Three-tier cross-module API: `*ContractApi` defines the shared contract, the module-side `*Api` extends it to add module-internal methods, and `Local*` signals same-JVM implementation. A future `Remote*Api` can sit alongside when modules move to RPC. DTO suffix `*RpcRequest`/`*RpcResponse` for cross-module payloads to keep them separate from HTTP `*Request`/`*Response`. |
| `*Service` + `Default*Service` | Spring convention (`DefaultListableBeanFactory`); avoids `*Impl` smell while keeping interface for AOP/mocking |
| MyBatis-Plus over JPA | Legacy upstream baseline; better fit for legacy PostgreSQL schema |
| `focela.*` config prefix | Single namespace for application properties |

## Open gaps

| Gap | Impact | Tracker |
|---|---|---|
| No Flyway/Liquibase schema migration | Schema drift between H2 (test) and PostgreSQL (prod); manual provisioning | `docs/REFACTOR_PLAN_LEVEL_B.md` (Mức C+) |
| Chinese comments throughout | Onboarding friction for non-CN devs | — |
| No CI/CD | Manual gate only | Postponed |

Optional modules that are not part of the active Maven reactor are tracked in
[MODULE_BOUNDARIES.md](docs/MODULE_BOUNDARIES.md) instead of being kept as
commented-out POM entries.

## Useful URLs

- Swagger UI: `http://127.0.0.1:48080/doc.html` (when running)
- OpenAPI JSON: `http://127.0.0.1:48080/v3/api-docs`
- Druid SQL monitor: `http://127.0.0.1:48080/druid/`
- Actuator: `http://127.0.0.1:48080/actuator/info`

## History

The codebase was rebranded from its legacy upstream baseline to Focela in a series of refactor phases on `feature/SKF-1`:

- **Phase A** — Rebrand Maven coordinates, Java base package, class names, config prefix (`legacy.*` → `focela.*`)
- **Phase B** — Standardize naming: `dal/dataobject/` → `domain/entity/`, `vo/` → `dto/`, `*DO` → `*Entity`, `*ReqVO/*RespVO` → `*Request/*Response`
- **Phase C** — International naming: `dept` → `department`, `dict` → `dictionary`, `*ServiceImpl` → `Default*Service`, etc.
- Codegen module removed (FE migrating to React)

See `git log --oneline main..feature/SKF-1` for full commit history.
