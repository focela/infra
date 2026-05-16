# Module Template

Standard layout for a business module in `apps/api/`. Every new module
must follow this template; the existing modules `focela-system` and
`focela-infra` are the canonical references.

Conventions here reflect the post-refactor state on `feature/SKF-1`
(international Spring/JHipster alignment — no `module.` or `framework.`
segments in package paths, single Maven artifact per module).

---

## 1. Maven structure

Each business module is a single Maven artifact (no `-api`/`-biz`
split). The artifact name has no `module-` infix.

```
focela-<X>/                                   one jar; depends on focela-common + starters
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/focela/platform/<X>/    package path mirrors the artifact name
    │   └── resources/
    │       ├── application-*.yaml           (only in focela-server)
    │       └── META-INF/services/           (if module exposes any SPI)
    └── test/
        ├── java/com/focela/platform/<X>/    test packages mirror main
        └── resources/sql/                   in-memory test schema
```

Steps to wire a new module into the reactor:

1. Create `apps/api/focela-<X>/` from a copy of `focela-infra/`.
2. Replace `focela-infra` with `focela-<X>` in the copied pom and in
   every package path under `src/`.
3. Add `<module>focela-<X></module>` to `apps/api/pom.xml`.
4. Add `<dependency>` on the new module to
   [`focela-server/pom.xml`](../focela-server/pom.xml).
5. Add the package to `@SpringBootApplication(scanBasePackages = …)` in
   [`FocelaServerApplication`](../focela-server/src/main/java/com/focela/platform/server/FocelaServerApplication.java).

---

## 2. Package skeleton inside `com.focela.platform.<X>/`

Every module must use this top-level skeleton — same names, same
purposes, regardless of which features the module owns. Missing
sub-packages are allowed when the module simply has no code of that
kind; **do not invent new top-level packages**.

```
com/focela/platform/<X>/
├── api/                  in-process implementations of cross-module *Api contracts
│   └── <feature>/
│       ├── Local<Feature>Api.java
│       └── dto/              not used currently — DTOs live in focela-common/api/
├── config/               Spring @Configuration classes + their supporting code
│   └── <feature>/
│       ├── <Feature>Configuration.java
│       ├── client/              third-party client wrappers (if any)
│       ├── enums/               feature-internal enums
│       └── property/            @ConfigurationProperties classes
├── constants/            module-wide constants (e.g., RedisKeyConstants)
├── controller/
│   ├── admin/<feature>/        backoffice REST endpoints
│   │   ├── <Feature>Controller.java
│   │   └── dto/                request/response DTOs for this controller
│   └── app/<feature>/          end-user REST endpoints (subset of features)
│       ├── <Feature>Controller.java
│       └── dto/
├── converter/<feature>/  MapStruct between Entity ↔ DTO
├── entity/<feature>/     MyBatis-Plus entities (suffix *Entity)
├── enums/                module-wide enums (not feature-internal)
├── job/                  Quartz jobs (flat or `<feature>/` subfolders for clean jobs)
├── mq/
│   ├── producer/<feature>/
│   ├── consumer/<feature>/
│   └── message/<feature>/
├── repository/
│   ├── mapper/<feature>/       MyBatis-Plus mappers (suffix *Mapper)
│   └── redis/<feature>/        Redis-backed repositories (suffix *RedisRepository) — optional
└── service/<feature>/    service interface + Default<Feature>Service impl
```

---

## 3. Naming conventions (must match exactly)

| Where | Suffix / Prefix | Example |
|---|---|---|
| REST controller | `*Controller` | `UserController` |
| Service interface | `*Service` | `UserService` |
| Service implementation | `Default*Service` | `DefaultUserService` |
| MyBatis-Plus mapper | `*Mapper` | `UserMapper` |
| Redis-backed access | `*RedisRepository` | `OAuth2AccessTokenRedisRepository` |
| MapStruct converter | `*Converter` | `UserConverter` |
| MyBatis-Plus entity | `*Entity` | `UserEntity` |
| Module-internal enum | none | `CommonStatusEnum` |
| Spring auto-configuration | `*Configuration` or `*AutoConfiguration` | `SecurityConfiguration`, `FocelaFileAutoConfiguration` |
| `@ConfigurationProperties` POJO | `*Properties` | `SmsCodeProperties` |
| Quartz job | `*Job` | `JobLogCleanJob` |
| Cross-module API interface (in `focela-common/api/`) | `*ContractApi` | `ApiErrorLogContractApi` |
| In-JVM impl of a `*ContractApi` (in `<X>/api/`) | `Local*Api` | `LocalApiErrorLogApi` |

### DTO suffix matrix

DTO suffixes encode both **shape** and **layer** at a glance — never use
plain `*VO` or `*DTO`:

| Suffix | Shape | Where it lives |
|---|---|---|
| `*Request` | generic REST request body | `controller/{admin,app}/<feature>/dto/` |
| `*SaveRequest` | create-or-update request | `controller/{admin,app}/<feature>/dto/` |
| `*PageRequest` | paged-query parameters | `controller/{admin,app}/<feature>/dto/` |
| `*Response` | REST response body | `controller/{admin,app}/<feature>/dto/` |
| `*RpcRequest` | cross-module request (jumps Maven artifact boundary) | `focela-common/src/.../common/api/<module>/<feature>/dto/` |
| `*RpcResponse` | cross-module response | `focela-common/src/.../common/api/<module>/<feature>/dto/` |
| `*ExcelDto` | row in an Excel import/export | `controller/admin/<feature>/dto/` (rare) |

The `Rpc` infix is **mandatory** for any DTO that crosses a Maven
artifact boundary — it prevents accidental name collision with an
intra-module `*Request`/`*Response` of the same domain concept (e.g.,
`UserResponse` already exists in `focela-system`; the cross-module
counterpart is `UserRpcResponse`).

---

## 4. Layer rules

- `controller/` is the only outward-facing layer. It may import
  `service/`, `entity/`, `converter/`, and DTOs.
- `service/` must not import controllers. It returns `*Entity` to the
  controller, which then converts via MapStruct or `BeanUtils.toBean`
  into a `*Response`.
- `repository/mapper/` and `repository/redis/` are imported only by
  `service/` (never by controllers).
- Cross-module access goes through `focela-common/api/<module>/<Feature>ContractApi`
  — never reach into another module's `service/` or `repository/`.
- `config/<feature>/` may import anything it configures. Other code may
  import beans created by `config/<feature>/` but should not import the
  `@Configuration` class itself.
- `mq/{producer,consumer,message}/<feature>/` mirrors the service-layer
  grouping — same `<feature>` directory name.

---

## 5. Admin vs. app controller split

`controller/admin/` and `controller/app/` are kept separate **on
purpose**. Each module decides which features it exposes to which
audience:

- `controller/admin/<feature>/` — backoffice/admin UI. URL prefix
  `/system/<feature>`, `/infra/<feature>`, … (no leading `/admin-api`
  because the global `server.servlet.context-path` adds it).
- `controller/app/<feature>/` — end-user (mobile / public) UI. URL
  prefix `/system/<feature>`, served under `/app-api` context.

Same URL path is allowed to appear in both `admin/` and `app/` because
they live under different context paths. Security filter chains apply
different policies per context.

Do **not** consolidate both into a single `controller/` package — the
split is what allows Spring Security and rate-limit policies to scope
themselves by audience.

---

## 6. Feature subpackage naming consistency

When a feature exists in more than one layer, it must use the same
subpackage name everywhere. Example for `logger` in `focela-infra`:

- `controller/admin/logger/ApiAccessLogController.java`
- `service/logger/DefaultApiAccessLogService.java`
- `entity/logger/ApiAccessLogEntity.java`
- `repository/mapper/logger/ApiAccessLogMapper.java`
- `converter/logger/ApiAccessLogConverter.java`
- `mq/{producer,consumer,message}/logger/` (if MQ involved)
- `job/logger/` (if scheduled jobs exist)

This rule is what lets you predict every file's location from the
feature name alone.

---

## 7. Where DTOs go — quick lookup

| Use case | Location |
|---|---|
| Admin REST API request/response | `controller/admin/<feature>/dto/` |
| End-user REST API request/response | `controller/app/<feature>/dto/` |
| Cross-module RPC contract DTO | `focela-common/.../common/api/<module>/<feature>/dto/` |
| Internal entity (DB row) | `entity/<feature>/` |
| Third-party client DTO (e.g., SMS provider request body) | `config/<feature>/client/dto/` |

---

## 8. What to avoid

- **No `module.` or `framework.` segment** in package paths — package
  starts with `com.focela.platform.<feature>` directly.
- **No `*VO`, `*DO`, `*Impl`** suffixes — use the convention table above.
- **No `dal/` package** — use `repository/`.
- **No `*Api` suffix on intra-module classes** — `*Api` is reserved for
  the cross-module contract interface in `focela-common/api/`.
- **No nested `config/<feature>/config/`** — `@Configuration` classes
  sit directly in `config/<feature>/`.
- **No `core/` subpackage inside `config/<feature>/`** — supporting code
  sits directly under `config/<feature>/` (alongside the auto-config
  class), grouped by purpose: `client/`, `enums/`, `property/`, etc.
- **No `@author` tags** — git history is the authoritative author log.
- **No Chinese in comments / annotation strings** — see
  [`TRANSLATION_GLOSSARY.md`](TRANSLATION_GLOSSARY.md) for the migration
  terminology.

---

## 9. Approved naming decisions (authoritative)

The naming rules below are locked in. ArchUnit enforces a subset
(see `ArchitectureRules.java`); the rest is reviewed manually.

| Decision | Rule | Rationale |
|---|---|---|
| A | Service implementation uses `Default*Service`, not `*ServiceImpl` | Matches Spring core (`DefaultListableBeanFactory`, `DefaultSecurityFilterChain`); 100 % project consistency already; semantic — "this is the default impl, can be overridden". |
| B | `Focela*AutoConfiguration` — framework starter Spring Boot auto-config (must have entry in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`). `*Configuration` (no `Focela` prefix) — module-internal `@Configuration` class loaded via component scan / `@Import`. | Clean separation: SPI vs in-context. Reader can tell at a glance whether a class is auto-wired by Spring Boot's starter mechanism or by the module's own scanning. |
| C | Cross-module API surface lives in `focela-common/api/<module>/<feature>/*ContractApi` (interface) + `<module>/api/<feature>/Local*Api` (in-JVM impl). Module-internal API interfaces declared inside `<module>/api/` are an interim pattern — new code MUST use `*ContractApi`. | Open architecture for future microservice extraction (swap `Local*Api` for `Remote*Api`). |
| D | yudao `*X` extension classes (`BaseMapperX`, `QueryWrapperX`, `LambdaQueryWrapperX`, `MPJLambdaWrapperX`) — keep as-is; documented in `TRANSLATION_GLOSSARY.md`. | Renaming cascades through ~40 mappers; payoff too low. |
| E | Integration tests use suffix `*IT` (Maven Failsafe convention). Unit tests use `*Test`. | `*IT` runs at phase `verify`, not `test` — separates fast unit feedback loop from slow IT loop. |
| F | `controller/admin/` and `controller/app/` split is kept. Same URL path may appear in both because they live under different `server.servlet.context-path` (`/admin-api` vs `/app-api`). Security filter chains and rate-limit policies apply per audience. | Pattern used by Keycloak (`services/resources/admin` + `account`), GitLab (`api/v4/admin`), Discourse, WordPress. Acceptable in enterprise apps where backoffice and end-user have different security policies. |
| G | `ErrorCodeConstants` per module uses module prefix: `SystemErrorCodeConstants`, `InfraErrorCodeConstants`. `GlobalErrorCodeConstants` (no prefix) is reserved for framework-wide HTTP error codes in `focela-common`. | Removes import-collision risk when two modules' error code classes share the same name. (Migration is a separate phase.) |

## 10. Location rules (enforced by ArchUnit)

| Class suffix | Required package |
|---|---|
| `*Controller` | `..controller.admin..` or `..controller.app..` |
| `*Entity` | `..entity..` |
| `*Mapper` | `..repository.mapper..` |
| `*Configuration` | `..config..` |
| `*Constants` | `..constants..` |
| `*Enum` | `..enums..` |

Violations of these rules fail the build via
`SystemArchitectureTest` and `InfraArchitectureTest`.

## 11. Acronym style (Oracle Java Style)

| Pattern | Correct | Incorrect |
|---|---|---|
| Standalone acronym | `XmlParser`, `HtmlEncoder`, `DbClient` | `XMLParser`, `HTMLEncoder`, `DBClient` |
| Acronym mid-identifier | `parseXml`, `toHtml`, `loadDb` | `parseXML`, `toHTML`, `loadDB` |
| Brand names (keep as-is) | `OAuth2`, `S3`, `MQ`, `JSON` (in proper noun like `JsonUtils`) | — |
| Compound acronym | `MpjLambda` (treat as one acronym) | `MPJLambda` |

Exception: `OAuth2`, `S3FileClient`, `S3FileClientConfig` (brand names);
the four `*X` yudao extension classes (see Decision D).

## 12. Checklist when adding a new module

(Section number changed from 9 — the new sections 9–11 above must be read first.)

- [ ] Module directory `focela-<X>/` created from `focela-infra/` template
- [ ] `apps/api/pom.xml` lists `<module>focela-<X></module>`
- [ ] `focela-dependencies/pom.xml` declares the artifact in `<dependencyManagement>` (if other modules will depend on it)
- [ ] `focela-server/pom.xml` adds the `<dependency>`
- [ ] `FocelaServerApplication.scanBasePackages` includes `${focela.info.base-package}.<X>`
- [ ] Top-level package skeleton (§2) created — empty sub-packages OK, but no rogue top-level packages
- [ ] At least one feature implemented end-to-end (`controller → service → repository → entity`) as a reference
- [ ] Tests use `focela-spring-boot-starter-test` base classes
- [ ] `mvn clean test` is green
- [ ] No Chinese remaining (`grep -rP "[\\x{4e00}-\\x{9fff}]"` returns nothing)
