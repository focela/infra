# Module Template

Standard layout for a business module in `apps/api/`. Every new module
should follow this template; existing modules (`focela-module-infra`,
`focela-module-system`) already do.

## 1. Maven structure

Each business module is a Maven **aggregator** with two children — an
`-api` artifact (public contracts) and a `-biz` artifact (implementations):

```
focela-module-<X>/                          aggregator (packaging=pom)
├── pom.xml
├── focela-module-<X>-api/                  ★ THIN public contracts
│   ├── pom.xml                             jar; deps: focela-common + jakarta.validation
│   └── src/main/java/com/focela/platform/module/<X>/api/
│       ├── <Domain>Api.java                public service interface
│       ├── dto/
│       │   ├── <Domain>ReqDTO.java         cross-module request DTOs
│       │   └── <Domain>RespDTO.java        cross-module response DTOs
│       ├── enums/                          shared enums referenced by DTOs
│       └── package-info.java
└── focela-module-<X>-biz/                  implementations
    ├── pom.xml                             jar; deps: -api + everything else
    └── src/main/java/com/focela/platform/module/<X>/
        ├── api/<Domain>/
        │   └── Local<Domain>Api.java       in-JVM impl of api/<Domain>Api
        ├── controller/admin/<Domain>/
        │   ├── <Domain>Controller.java
        │   └── dto/<Domain>/
        │       ├── <Domain>Request.java    controller-tier HTTP DTOs
        │       └── <Domain>Response.java
        ├── service/<Domain>/
        │   ├── <Domain>Service.java        internal service interface
        │   └── Default<Domain>Service.java internal service impl
        ├── repository/
        │   ├── entity/<Domain>Entity.java  MyBatis entity (suffix Entity, not DO)
        │   └── mapper/<Domain>Mapper.java  MyBatis Plus mapper
        ├── converter/<Domain>Converter.java MapStruct between layers
        ├── enums/                          INTERNAL enums (not exposed)
        ├── framework/                      Spring config, security, MQ wiring
        ├── job/                            scheduled jobs
        └── mq/                             MQ listeners
```

Direct children of `apps/api/pom.xml` list only the aggregator (`focela-module-<X>`),
not the two children — Maven discovers them via the aggregator.

## 2. Why split into `-api` and `-biz`

| Property | Single artifact | -api + -biz split |
| --- | --- | --- |
| Other modules can reach internals | yes (anything is importable) | no — only -api is on the consumer classpath at compile time |
| Microservice extraction | rewrite consumer imports | swap `Local<Domain>Api` for `Remote<Domain>Api`, -api stays |
| Build time | one jar | two jars |
| Naming clarity | mixed | suffix indicates layer (see §4) |

Other modules depend on `focela-module-<X>-api` for compile time;
`focela-server` adds `focela-module-<X>-biz` so the runtime implementation
is on the classpath and Spring wires `Local<Domain>Api` automatically.

## 3. Layer rules

- A class in `-api` may import only:
  - `focela-common` (PageParam, PageResult, ErrorCode, common enums)
  - `jakarta.validation.*`
  - Other `*-api` artifacts when one module's API depends on another
- A class in `-biz` may import anything in `-api` and any `-biz` package
  of the same module. It may import other modules' `-api` artifacts but
  not their `-biz`.
- `controller/` may not import `repository/entity/*` directly — go through
  `service/`. (No DAO in controller.)
- `service/` returns `<Domain>Entity` to controllers; controllers convert
  to `<Domain>Response` via `BeanUtils.toBean` or MapStruct.
- Cross-module calls go through `<Domain>Api` (the public interface in -api),
  not through `<Domain>Service` (internal to its module).

## 4. Naming conventions

| Where | Class suffix | Example |
| --- | --- | --- |
| `-api/api/dto/` | `ReqDTO` / `RespDTO` | `OperateLogPageReqDTO`, `AdminUserRespDTO` |
| `-biz/controller/.../dto/` | `Request` / `Response` | `UserPageRequest`, `UserResponse` |
| `-biz/repository/entity/` | `Entity` | `AdminUserEntity` |
| Public API interface | `Api` | `AdminUserApi` |
| In-JVM impl of an `Api` | `Local…Api` | `LocalAdminUserApi` |
| Internal service interface | `Service` | `AdminUserService` |
| Internal service impl | `Default…Service` | `DefaultAdminUserService` |
| MyBatis mapper | `Mapper` | `AdminUserMapper` |
| MapStruct converter | `Converter` | `AdminUserConverter` |

The two DTO families on purpose share *kind* but not *suffix*: the suffix
identifies the layer at a glance, and it resolves the import-collision
problem that arises when both layers genuinely model the same domain
concept (e.g. `Department`).

## 5. Steps to add a new module

1. `cp -r focela-module-infra/ focela-module-<X>/` then clean out the
   business code in -biz, keep the skeleton.
2. In `apps/api/pom.xml`, add `<module>focela-module-<X></module>`.
3. In `focela-server/pom.xml`, add the `-api` AND `-biz` dependencies.
4. In each consumer module, depend on `focela-module-<X>-api` (compile)
   only — never on `-biz`.
5. Write your public interface(s) in `-api/api/<Domain>Api.java` plus
   request/response DTOs in `-api/api/<Domain>/dto/`.
6. Implement `Local<Domain>Api` in `-biz/api/<Domain>/`, delegating to
   internal `<Domain>Service`.
7. Build with `./mvnw install -pl focela-module-<X> -am -DskipTests`.

## 6. Files to translate / clean up when copying

The previous yudao→Focela rename already happened, but if you import a
new module from upstream, expect to:

- Rename top-level pom artifactId / description.
- Rewrite all `cn.iocoder.yudao.*` packages to `com.focela.platform.*`.
- Move `*DO` → `*Entity`, `dal/` → `repository/`, `vo/` → `dto/`,
  `*ServiceImpl` → `Default*Service`, `*ApiImpl` → `Local*Api`.
- Translate Chinese in `@Schema`/`@Operation`/`@Parameter` annotations,
  validation messages, log/exception messages, and Javadoc.
- Strip `@author` tags (git history has authorship).

The earlier refactor commits in `feature/SKF-1` are a reference for
every one of these steps.
