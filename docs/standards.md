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
| Compose file | `compose.yaml` (v2 spec, no `version:` key) | — |
| Backup script | `backup/<stack>-backup.sh` | `backup/proxy-backup.sh` |
| Feature branch | `feature/INF-<n>` | `feature/INF-3` |
| Develop branch | `feature/INF-<n>_1` | `feature/INF-3_1` |

The backup script and Makefile `s=<stack>` argument use the stack
directory name, not the tool's product name (e.g. `proxy`, not `npm`).

## Data and Database

- Each tool owns its data. Do not share a database between tools.
- A tool that needs a database runs its own, inside its own stack.
- For Nginx Proxy Manager, SQLite is used (its default). Switch to
  MariaDB only if an external or centralized database is required — not
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
2. PR into `main`; address Codex review until clean; merge (squash).
3. Cherry-pick to `develop` via `feature/INF-<n>_1`; PR into `develop`.

## Code Review

- Codex reviews every PR automatically.
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
