# Contributing

This repository follows a fixed branch model and PR workflow. Detailed
conventions (commit format, naming, image pinning) live in
[`docs/standards.md`](docs/standards.md). This file covers the workflow.

## Branch Model

| Branch | Purpose |
|---|---|
| `main` | Production. Deployed to live VMs. |
| `staging` | Pre-production validation. |
| `develop` | Integration branch for ongoing work. |

Feature branches are created from `main`:

```bash
git checkout -b feature/INF-<n> origin/main
```

## Workflow

1. Create an issue in Jira (`INF-<n>`).
2. Branch from `main`: `feature/INF-<n>`.
3. Commit using the [commit convention](docs/standards.md#commit-convention).
4. Open a PR into `main` following the
   [PR convention](docs/standards.md#pull-request-convention).
5. Address Codex review feedback until no issues remain.
6. After merge to `main`, cherry-pick to `develop` via a
   `feature/INF-<n>_1` branch and open a second PR into `develop`.

## Adding a New Stack

1. Create `stacks/<tool>/compose.yaml` joining the `edge` network.
2. Create `stacks/<tool>/.env.example` documenting required variables.
3. Create `stacks/<tool>/README.md` with per-stack configuration and operations.
4. Add `backup/<tool>-backup.sh` if the stack has persistent data.
5. Update the stack table in `README.md` and `README.vi.md`.

## Local Validation

Before opening a PR:

```bash
# Validate compose syntax
docker compose -f stacks/<tool>/compose.yaml config

# Validate shell scripts
bash -n backup/<tool>-backup.sh
```
