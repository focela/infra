# Technical Debt Backlog

Living catalog of known technical debt items in the `apps/api` codebase. Each
entry is meant to be lifted into the team issue tracker before being acted on;
the file itself is **not** a substitute for ticketing — it is a single place to
see the shape of outstanding work.

Last audited: 2026-05-24 (Sprint 2, Phase 5).

---

## 1. Coverage baseline (JaCoCo)

JaCoCo Maven plugin is wired in `apps/api/pom.xml` (`jacoco-maven-plugin
0.8.13`), runs at the `test` phase, writes HTML to
`<module>/target/site/jacoco/index.html`.

Initial line-coverage baseline (computed from `jacoco.csv`):

| Module          | Lines covered | Total | Line coverage |
|-----------------|---------------|-------|---------------|
| `focela-system` | 11,662        | 18,469 | **63.1%**    |
| `focela-infra`  | 2,267         | 5,035  | **45.0%**    |

**No threshold is enforced yet.** Once the team agrees on a target (suggested
floor: 60% line for `focela-system`, 40% for `focela-infra`), add a
`jacoco:check` execution with `<limit>` rules. Doing this before agreeing on
the floor would either set it too low to matter or break CI on day one.

Suggested follow-ups (separate tickets):

- Raise `focela-infra` coverage above 50% (job + config layers are the gap).
- Enable `jacoco:check` with team-agreed thresholds.
- Optionally add `jacoco:report-aggregate` in the parent POM for a single
  combined HTML report.

---

## 2. TODO / FIXME in production code

Twelve real TODO comments live in production sources (uppercase `XXX` strings
in Swagger examples and date-format constants are false positives and not
listed). Grouped by category for triage:

### 2.1 Waiting on upstream library

| File:line | Comment | Notes |
|---|---|---|
| `focela-system/.../config/justauth/AuthRequestFactory.java:46` | "waiting for official 1.4.1 release!" | Tracks JustAuth 1.4.1 — bump once available |
| `focela-system/.../config/justauth/FocelaJustAuthConfiguration.java:14` | Same JustAuth note (in class Javadoc) | Same upstream dependency |
| `focela-framework/focela-spring-boot-starter-monitor/.../tracer/config/FocelaTracerAutoConfiguration.java:25` | "SkyWalking not compatible with latest opentracing; opentracing no longer maintained. Migrate to opentelemetry" | Larger migration — separate epic |

### 2.2 Enhancement (intentional `// TODO Focela:` tag)

| File:line | Comment | Notes |
|---|---|---|
| `focela-system/.../service/sms/DefaultSmsCodeService.java:61` | "enhancement, daily quota per IP" | Anti-abuse follow-up |
| `focela-system/.../service/sms/DefaultSmsCodeService.java:62` | "enhancement, hourly quota per IP" | Pair with above |

### 2.3 Genuine code-level debt

| File:line | Comment | Notes |
|---|---|---|
| `focela-framework/focela-common/.../utils/cache/CacheUtils.java:42` | "consider making thread pool configurable" | Currently `Executors.newCachedThreadPool()` — unbounded |
| `focela-framework/focela-common/.../exception/ErrorCode.java:13` | "Error codes modeled as objects to leave room for future i18n support" | Documentation TODO, not a bug |
| `focela-framework/focela-spring-boot-starter-mq/.../redis/core/stream/AbstractRedisStreamMessageListener.java:70` | "still to consider:" (followed by multi-item list) | Open question; clarify or remove |
| `focela-framework/focela-spring-boot-starter-data-permission/.../rule/department/DepartmentDataPermissionRule.java:134` | "when no condition can be built, do not throw; instead return no data" | Behavior decision pending |
| `focela-framework/focela-spring-boot-starter-tenant/.../core/job/TenantJobAspect.java:44` | "first use parallel to run in parallel; 1) multiple tenants share a single execution log; 2) handle exception cases" | Performance + observability follow-up |
| `focela-framework/focela-spring-boot-starter-mybatis/.../core/query/QueryWrapperX.java:145` | "not a perfect solution; needs more thought. With multiple data sources of different types, the syntax for limiting rows differs" | DB-portability concern |
| `focela-framework/focela-spring-boot-starter-mybatis/.../config/IdTypeEnvironmentPostProcessor.java:42` | "no particularly suitable place yet; keep here for now" | File-organization debt |

### 2.4 Recommended comment format going forward

Once these items have tickets, update the comment to reference them:

```java
// TODO #123: Daily quota per IP — see SmsCodeProperties.dailyLimit
```

The architecture tests already enforce this for future code via the
`LEGACY_TEST_METHOD_PATTERN` exclusion in `ArchitectureRules`.

---

## 3. `@SuppressWarnings` inventory

51 production files use `@SuppressWarnings`. Distribution by warning kind:

| Warning kind                                    | Files | Category                              | Recommended action |
|-------------------------------------------------|-------|---------------------------------------|--------------------|
| `deprecation`                                   | 31    | Library/Spring deprecations           | Re-evaluate after each Spring Boot minor upgrade |
| `unchecked` (alone or combined with `rawtypes`) | 17    | Generic type erasure                  | Accept — Java limitation |
| `PatternVariableCanBeUsed`                      | 7     | IDEA suggesting Java 16 switch syntax | Could refactor when touching the file |
| `NullableProblems`                              | 7     | External library nullness mismatch    | Accept — out of our control |
| `InstantiationOfUtilityClass`                   | 4     | Interfaces used as constant holders   | False positive — accept |
| `SpringJavaInjectionPointsAutowiringInspection` | 3     | IDEA inspection false positive        | Accept — IDE issue |
| `SpringJavaAutowiredFieldsWarningInspection`    | 3     | IDEA inspection false positive        | Accept — IDE issue |
| `EnhancedSwitchMigration`                       | 3     | IDEA suggesting newer switch syntax   | Could refactor when touching the file |
| `DuplicatedCode`                                | 3     | Acceptable duplication                | Accept |
| `JavadocReference`                              | 2     | Javadoc to deprecated/removed APIs    | Re-evaluate alongside deprecation work |
| `resource`                                      | 1     | try-with-resources not applicable     | Accept |

**Categories summary:**

- **31 deprecation suppressions** account for 61% — these will shrink naturally
  as Spring Boot upgrades land. Track them as a single "Spring 3.6 upgrade"
  epic rather than as individual issues.
- **17 unchecked / 4 utility-class / 6 IDE-false-positive** suppressions are
  inherent to the language or external libraries — leave as-is.
- **10 `PatternVariableCanBeUsed` + `EnhancedSwitchMigration`** are
  IDE-suggested modernizations of perfectly correct Java 17 code — convert
  opportunistically when editing each file, not in a dedicated sweep.

---

## 4. Nullness annotation discipline

72 files currently import a nullness annotation, drawn from three competing
dialects:

- `jakarta.annotation.@Nullable` / `@Nonnull` — **the chosen standard** (see
  `docs/MODULE_TEMPLATE.md` §12.8).
- `org.springframework.lang.@Nullable` — appears in older files; do not
  introduce new occurrences.
- `javax.annotation.@Nullable` (JSR-305) — legacy.

**Plan:** Incremental — when you are already editing a file for another
reason, switch its nullness annotation imports to Jakarta. **No big-bang
migration**. The team can revisit when the file count drops below ~10.

---

## 5. Secrets externalization status

`application.yaml` now uses the `${ENV_VAR:fallback}` pattern for every
hardcoded credential (22 entries across the MyBatis encryptor, Spring AI
clients, focela AI clients, API encryption keys, and express-delivery
clients). Runtime behavior is unchanged because the existing value is the
fallback default — operations can override per environment without touching
the file.

**Follow-up still owed:**

- `application-dev.yaml` and `application-local.yaml` contain dev/local
  credentials that are NOT yet externalized:
  - DB / Redis / RabbitMQ passwords (`123456`, `rabbit`, `admin`, `guest`)
  - WeChat MP / Mini Program `app-id` + `secret` pairs
  - OAuth2 `client-secret` values in justauth config
  - The values are dev-only and clearly marked, but still belong in env
    vars before any prod deployment uses these profiles.
- Once the team confirms which fallback defaults are stale or revoked
  template values, strip the defaults so the application fails fast on
  missing env vars instead of running with a known-bad credential.
- Add `application-prod.yaml.example` as a committed template (mirroring
  the env vars introduced above) so ops have a starting point.

## 6. Items deliberately deferred

The following are known but explicitly **not** in this backlog because doing
them would introduce risk disproportionate to the benefit. They are recorded
here so they do not get rediscovered by future audits.

| Item | Why deferred |
|---|---|
| Rename `Default*Service` → `*ServiceImpl` | 58/58 services use `Default*` — `*Impl` is the anti-pattern; reversing is regression |
| Rename `BaseMapperX` / `QueryWrapperX` family | Touches ~50 mapper interfaces for purely cosmetic gain |
| Rename `focela-system` → `focela-module-system` | Breaks Maven artifact coordinates and the BOM |
| Migrate `dictType`/`deptId` legacy abbreviations | Bound to DB column names; allow-listed by ArchUnit |
| Split `DefaultUserService` (552 lines) | Domain complexity is intrinsic — splitting would scatter cohesion |
| Add license headers | Internal project — add only when open-sourcing |
| Generate dependency graph diagrams | ASCII diagrams in README + MODULE_TEMPLATE.md already cover the territory |
