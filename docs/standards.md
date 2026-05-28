# Standards

Conventions for stacks, scripts, commits, and pull requests in this
repository.

## Image Pinning

- Pin every image to an explicit version tag (e.g.
  `jc21/nginx-proxy-manager:2.12.6`). Never use `latest`.
- Bump tags deliberately when a security fix or required feature lands,
  in a dedicated PR.

## Naming

| Item | Pattern | Example |
|---|---|---|
| Stack directory | `stacks/<tool>/` | `stacks/proxy/` |
| Stack README | `stacks/<tool>/README.md` | `stacks/proxy/README.md` |
| Compose file | `compose.yaml` (v2 spec, no `version:` key) | n/a |
| Backup script | `backup/<stack>-backup.sh` | `backup/proxy-backup.sh` |
| Feature branch | `feature/INF-<n>` | `feature/INF-3` |
| Develop branch | `feature/INF-<n>_1` | `feature/INF-3_1` |

The backup script and Makefile `s=<stack>` argument use the stack
directory name, not the tool's product name (e.g. `proxy`, not `npm`).
For new stacks, keep the Compose project name, primary service key, and
container name aligned with the stack directory unless the product requires a
different service name. Document any exception in the stack README.

For multi-service tools (e.g. Prometheus + Grafana + Alertmanager), place
services in a single `stacks/<group>/compose.yaml` when they share a release
lifecycle and backup boundary. Split into separate stacks
(`stacks/<group>-<service>/`) when services upgrade or back up independently.

## Stack Networking

- Default stack services join the host-local `edge` Docker network.
- `network_mode: host` is allowed for stacks that require host networking
  (for example VPN services or host-level agents).
- Host-network stacks must document why host networking is required and which
  ports are expected to be reachable.

## Multi-Environment

Only `prod` is implemented today. Current stack runtime config uses
`stacks/<stack>/.env`. Do not introduce `.env.<env>` files or `STACK_ENV`
until a second environment is implemented.

When a new environment (e.g. `staging`) is added, implement the patterns below
in the same change across Makefile targets, backup scripts, and CI.

| Layer | Pattern | Example |
|---|---|---|
| Terraform | `terraform/envs/<env>/` directory per environment | `terraform/envs/staging/` |
| Stack runtime config | `.env.<env>` per stack, selected by `STACK_ENV` | `stacks/proxy/.env.staging` |
| Backup destination | `BACKUP_S3_BUCKET` per environment in `backup/.env` | `s3://infra-backups-staging/proxy-backups` |

- Default to `prod` until another environment exists. CI or operators set
  `STACK_ENV=staging` only after Makefile, backup, and CI support it.
- `.env.example` documents required variables; each environment overrides
  values in its own `.env.<env>` file (all gitignored).
- State buckets, IAM roles, and security groups are not shared across
  environments. Cross-account isolation is preferred over cross-VPC.

## Terraform Naming

| Item | Pattern | Example |
|---|---|---|
| Resource `Name` tag | `${prefix}-<type>[-<qualifier>]` | `infra-sg-vm-ops` |
| TF resource identifier | `snake_case`, matches VM or domain | `aws_security_group.vm_ops` |
| Variable | `snake_case`, descriptive | `vm_ops_instance_type` |
| Module | `snake_case`, matches VM | `module "vm_ops"` |
| Output | `<vm>_<attribute>` | `vm_ops_public_ip` |
| cloud-init file | `cloud-init-<vm>.sh` | `cloud-init-ops.sh` |

Singleton resources inside a module use `this` as the identifier
(e.g. `aws_instance.this`). Resources with multiple instances use
a descriptive name (e.g. `aws_subnet.public`, `aws_subnet.private`).

## Data and Database

- Each tool owns its data. Do not share a database between tools.
- A tool that needs a database runs its own, inside its own stack.
- Production stack data should use a documented host path, preferably an
  absolute path outside the source checkout (e.g.
  `/srv/infra/<stack>/data`).
- For Nginx Proxy Manager, SQLite is used (its default). Switch to
  MariaDB only if an external or centralized database is required; not
  for high availability, which a single MariaDB container does not
  provide.

## Backup Policy

- Every stack with persistent data has `backup/<stack>-backup.sh`.
- Backups upload to S3-compatible storage and prune by retention period
  (`BACKUP_RETENTION`, default 14 days).
- Run daily via cron and once manually before any image bump.
- Test restore periodically to confirm the archive is recoverable.
- Backups contain secrets (TLS private keys, databases). The S3 bucket
  must have server-side encryption (SSE) enabled.

## Language

- Code comments: English.
- Public documentation (README and `docs/`): bilingual, English and
  Vietnamese. English is the source of truth.

## Commit Convention

Conventional Commits with project-specific rules.

```
<type>(<scope>): <title>

- <per-file or per-change bullet>
- <per-file or per-change bullet>

<verification line>

Co-Authored-By: ...
```

Rules:
- `type`: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, `build`,
  `ci`.
- Title is fully lowercase, including proper nouns. Imperative mood.
- Body uses per-change bullets, hard-wrapped at ~65 characters.
- Footer states how the change was verified (e.g. `bash -n passes;
  --dry-run output checked`).

Example:

```
fix(backup): replace gnu-only grep -P with bash regex in prune loop

- grep -oP is unsupported on BSD/macOS; the || continue masked
  the failure so retention silently stopped pruning
- use a Bash regex match, portable across GNU and BSD

Verified: bash -n passes; --dry-run output checked.
```

## Pull Request Convention

Title format:

```
[Focela] [MAIN|DEV] INF-<n> <Capitalized description>
```

- `[MAIN]` for PRs into `main`, `[DEV]` for PRs into `develop`.
- The description after the issue number is capitalized.

Body follows the PR template: Description, Changes, Testing, Notes,
Related PRs, Jira.

Labels (apply the full set):

| Group | Examples |
|---|---|
| target | `target: main`, `target: develop` |
| type | `type: feature`, `type: bug`, `type: docs` |
| status | `status: ready for review` |
| priority | `priority: medium` |
| size | `size: xs`, `size: small` |
| area | `area: proxy`, `area: backup`, `area: tooling` |

- Assignee: the author.
- Reviewer: `pszyn`.
- Related PRs table links the `main` and `develop` PRs for the issue.

## Branch and Merge Flow

1. Branch from `main`: `feature/INF-<n>`.
2. PR into `main`; address review feedback until no blocking findings remain; merge (squash).
3. Cherry-pick to `develop` via `feature/INF-<n>_1`; PR into `develop`.

## Code Review

- Request Codex review on each PR before merge.
- Fix only valid, technically sound feedback; minimal change, no scope
  creep, no business-logic change.
- Reply to each thread with: root cause, fix applied, scope impact,
  validation. Resolve the thread, then comment `@codex review` to
  re-trigger review on the new commit.

## Changelog

`CHANGELOG.md` follows a version heading with `### Features`,
`### Enhancements`, and `### Bug Fixes` subsections (only those with
content are shown). Entries are conventional-commit bullets with a PR
link: `- feat(scope): description ([#N](url))`.

Scope:
- Track substantive changes: new stacks, capability or runtime-behavior
  changes, and bug fixes.
- Omit `docs`, `ci`, `build`, and `chore` changes; these are visible in
  git history and add noise to the changelog.

Process:
- After opening a PR (the PR number is then known), add the entry to
  `CHANGELOG.md` referencing that number and push it to the same branch
  before merge. Do not defer the entry to a later PR.
