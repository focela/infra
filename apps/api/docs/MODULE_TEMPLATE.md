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
│   │   ├── request/            REST request DTOs for this controller
│   │   └── response/           REST response DTOs for this controller
│   └── app/<feature>/          end-user REST endpoints (subset of features)
│       ├── <Feature>Controller.java
│       ├── request/
│       └── response/
├── converter/<feature>/  MapStruct between Entity ↔ DTO
├── domain/entity/<feature>/ MyBatis-Plus entities (suffix *Entity)
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
| `*Request` | generic REST request body | `controller/{admin,app}/<feature>/request/` |
| `*SaveRequest` | create-or-update request | `controller/{admin,app}/<feature>/request/` |
| `*PageRequest` | paged-query parameters | `controller/{admin,app}/<feature>/request/` |
| `*Response` | REST response body | `controller/{admin,app}/<feature>/response/` |
| `*RpcRequest` | cross-module request (jumps Maven artifact boundary) | `focela-common/src/.../common/api/<module>/<feature>/dto/` |
| `*RpcResponse` | cross-module response | `focela-common/src/.../common/api/<module>/<feature>/dto/` |
| `*ExcelRow` | row in an Excel import/export | `controller/admin/<feature>/request/` (rare) |

The `Rpc` infix is **mandatory** for any DTO that crosses a Maven
artifact boundary — it prevents accidental name collision with an
intra-module `*Request`/`*Response` of the same domain concept (e.g.,
`UserResponse` already exists in `focela-system`; the cross-module
counterpart is `UserRpcResponse`).

---

## 4. Layer rules

- `controller/` is the only outward-facing layer. It may import
  `service/`, `domain/entity/`, `converter/`, and DTOs.
- `service/` must not import controllers. It returns `*Entity` to the
  controller, which then converts via MapStruct or `BeanUtils.toBean`
  into a `*Response`.
- `repository/mapper/` and `repository/redis/` are imported only by
  `service/` (never by controllers).
- Cross-module access goes through either the other module's
  `<module>/api/` package (Spring Modulith style — preferred for simple
  cases) or via `focela-common/api/<module>/<Feature>ContractApi` (used
  when multiple consumers share a contract or microservice extraction is
  planned). See §9 Decision C for the choice criteria.
  Either way — never reach into another module's `service/`,
  `repository/`, `controller/`, `domain/entity/`, etc. The `.api.` package is
  the only legal entry point. (ArchUnit enforces this in
  `SystemArchitectureTest` / `InfraArchitectureTest`.)
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
- `domain/entity/logger/ApiAccessLogEntity.java`
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
| Admin REST API request/response | `controller/admin/<feature>/{request,response}/` |
| End-user REST API request/response | `controller/app/<feature>/{request,response}/` |
| Cross-module RPC contract DTO | `focela-common/.../common/api/<module>/<feature>/dto/` |
| Internal entity (DB row) | `domain/entity/<feature>/` |
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
| C | Cross-module API access has **two acceptable patterns** — choose per case, do not force-migrate existing code: **(C-direct)** module exposes interface in `<module>/api/<feature>/<X>Api`, consumer module imports directly across the Maven dependency (Spring Modulith style — the standard for modular monoliths); **(C-contract)** interface in `focela-common/api/<module>/<feature>/<X>ContractApi`, in-JVM impl `Local<X>Api` in the module (Spring Cloud OpenFeign / Camunda Engine API style — useful when you have multiple consumers or plan a microservice extraction). Existing code uses both: `WebSocketSenderApi` and `ConfigApi` follow (C-direct); `OperateLogContractApi`, `TenantContractApi`, etc. follow (C-contract). When a module API is intended for cross-module use AND has multiple consumers OR is on a microservice-extraction shortlist, prefer (C-contract). Otherwise (C-direct) is fine and preferred for simplicity. | Spring Modulith — the official Spring framework for modular monoliths (2023+) — explicitly uses (C-direct). (C-contract) is required only when binary boundary or remote-extraction is a goal. Forcing all module APIs through (C-contract) creates busywork without functional payoff for a single-deployment app. |
| D | yudao `*X` extension classes (`BaseMapperX`, `QueryWrapperX`, `LambdaQueryWrapperX`, `MPJLambdaWrapperX`) — keep as-is; documented in `TRANSLATION_GLOSSARY.md`. | Renaming cascades through ~40 mappers; payoff too low. |
| E | Integration tests use suffix `*IT` (Maven Failsafe convention). Unit tests use `*Test`. | `*IT` runs at phase `verify`, not `test` — separates fast unit feedback loop from slow IT loop. |
| F | `controller/admin/` and `controller/app/` split is kept. Same URL path may appear in both because they live under different `server.servlet.context-path` (`/admin-api` vs `/app-api`). Security filter chains and rate-limit policies apply per audience. | Pattern used by Keycloak (`services/resources/admin` + `account`), GitLab (`api/v4/admin`), Discourse, WordPress. Acceptable in enterprise apps where backoffice and end-user have different security policies. |
| G | `ErrorCodeConstants` per module uses module prefix: `SystemErrorCodeConstants`, `InfraErrorCodeConstants`. `GlobalErrorCodeConstants` (no prefix) is reserved for framework-wide HTTP error codes in `focela-common`. | Removes import-collision risk when two modules' error code classes share the same name. (Migration is a separate phase.) |

## 10. Location rules (enforced by ArchUnit)

| Class suffix | Required package |
|---|---|
| `*Controller` | `..controller.admin..` or `..controller.app..` |
| `*Entity` | `..domain.entity..` |
| `*Mapper` | `..repository.mapper..` |
| `*Configuration` | `..config..` |
| `*Constants` | `..constants..` |
| `*Enum` | `..enums..` |
| controller `*Request` | `..controller..request..` |
| controller `*Response` | `..controller..response..` |

Business modules must not create `controller/**/dto/` packages. REST
payloads are split by direction: request payloads live under
`request/`, response payloads live under `response/`. Cross-module RPC
payloads are the only DTOs that continue to use a `dto/` package.

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

## 12. Notes on internal structure

These are observations from the structural audit, codified here so
future contributors know which patterns are intentional.

### 12.1 `mq/` feature subdirectory convention

Within each module's `mq/` package, the canonical layout is:

```
mq/
├── producer/<feature>/<X>Producer.java
├── consumer/<feature>/<X>Consumer.java
└── message/<feature>/<X>Message.java
```

`focela-system/mq/` follows this (e.g., `producer/sms/`, `consumer/mail/`).
`focela-infra/mq/` currently has only empty placeholders — new MQ code
added here must follow the same `<sub>/<feature>/` shape.

### 12.2 Empty placeholder packages are acceptable

Some packages exist as forward-looking placeholders containing only a
`package-info.java`:

- `focela-system/job/` — no scheduled jobs yet.
- `focela-infra/mq/{producer,consumer,message}/` — no MQ code yet.

These are kept (rather than deleted) so that contributors can drop new
code into the expected location without having to recreate the
directory and rediscover the convention. Each has a `package-info.java`
documenting the intended use.

### 12.3 Hybrid `job/` layout when jobs span multiple features

`focela-infra/job/` mixes flat and feature-nested files on purpose:

```
job/
├── JobLogCleanJob.java       ← cleans the job feature's own table; lives at root because there is no sibling
└── logger/                   ← cleans the logger feature's tables
    ├── AccessLogCleanJob.java
    └── ErrorLogCleanJob.java
```

Each clean job lives next to the feature it serves. If a feature has
two or more jobs, they get their own `job/<feature>/` directory; a
single one stays at `job/` root rather than creating a one-file
subdirectory like `job/job/`.

### 12.4 `constants/` is flat

`*Constants` classes live directly under each module's `constants/`
package (no sub-directories). This is enforced by ArchUnit's
`CONSTANTS_RESIDES_IN_CONSTANTS_PACKAGE` rule. If a constants class
grows large enough that grouping helps, split it into multiple
top-level `*Constants` classes (e.g., `OAuth2ClientConstants` +
`OAuth2TokenConstants`) rather than nesting into a subdirectory.

### 12.5 Constructor injection (Spring official preference)

Spring DI in new code uses **constructor injection** via Lombok
`@RequiredArgsConstructor`, not `@Resource` / `@Autowired` field
injection:

```java
// PREFERRED
@Service
@RequiredArgsConstructor
public class DefaultXxxService implements XxxService {
    private final XxxMapper xxxMapper;
    private final RelatedService relatedService;
    // ...
}

// NOT preferred for new code
@Service
public class DefaultXxxService implements XxxService {
    @Resource
    private XxxMapper xxxMapper;
    @Resource
    private RelatedService relatedService;
}
```

Rationale:
- Required dependencies are non-null and final (enforced by Java).
- Missing beans fail fast at application startup, not on first call.
- Easy to construct in tests (no Spring context required).
- Compatible with Spring Framework's official recommendation since 5.x.

**Permitted exceptions to constructor injection** (still in the codebase):

1. **Optional bean** — when a dependency may be absent based on a feature
   flag. Use `@Autowired(required = false)` on the field (e.g.,
   `DefaultTenantService` for `focela.tenant.enable=false`,
   `DefaultSocialClientService` for the optional JustAuth client).
2. **Circular dependency** broken with a `@Lazy` proxy — use
   `@Resource @Lazy` on the field (e.g., `DefaultUserService`'s lazy
   reference to `TenantService`). Constructor injection cannot easily
   express `@Lazy` proxy without changing the constructor signature; the
   field-injection + `@Lazy` hybrid is the canonical Spring 6 pattern
   for cycle break.

If you need either exception, document the reason inline (a one-line
comment is sufficient).

## 13. Checklist when adding a new module

(Section number changed from 9 — the new sections 9–12 above must be read first.)

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
