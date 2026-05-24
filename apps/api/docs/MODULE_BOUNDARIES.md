# Module Boundaries

This document records the backend module catalogue and dependency boundaries for
the Focela API. Keep runtime wiring in Maven POM files limited to active modules;
future or optional modules belong here until they are implemented and tested.

## Active Runtime Modules

| Module | Responsibility | Boundary rule |
|---|---|---|
| `focela-framework` | Shared Spring Boot starters and common platform code | Must not depend on business modules |
| `focela-infra` | Infrastructure operations: config, file, job, logger, database metadata | May expose contracts through `com.focela.platform.infra.api` |
| `focela-system` | System capabilities: auth, user, department, permission, dictionary, mail, sms, notify, tenant | Should consume other modules through `.api` packages only |
| `focela-server` | Bootable Spring Boot application container | Composes active runtime modules |

## Optional Modules

These modules existed in the legacy upstream baseline or are planned extension
points, but they are not part of the active Maven reactor in this repository:

| Module | Status |
|---|---|
| `focela-member` | Deferred |
| `focela-bpm` | Deferred |
| `focela-report` | Deferred |
| `focela-mp` | Deferred |
| `focela-pay` | Deferred |
| `focela-mall` | Deferred umbrella module |
| `focela-product` | Deferred |
| `focela-promotion` | Deferred |
| `focela-trade` | Deferred |
| `focela-statistics` | Deferred |
| `focela-crm` | Deferred |
| `focela-erp` | Deferred |
| `focela-iot` | Deferred umbrella module |
| `focela-iot-biz` | Deferred |
| `focela-mes` | Deferred |
| `focela-ai` | Deferred |

## Dependency Direction

- `focela-server` may depend on runtime modules.
- Runtime modules should depend on framework starters and shared API contracts.
- Cross-module access must go through `.api` packages.
- Modules must not import another module's `controller`, `service`,
  `repository`, `converter`, or `domain/entity` packages.
- REST request/response payloads are controller-layer contracts. Existing legacy
  service/mapper use is allowlisted by architecture tests and should not be
  expanded.
