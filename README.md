# infra

[English](README.md) | [Tiếng Việt](README.vi.md)

Internal repository for self-hosted development tooling at Focela.

Manages Docker Compose stacks for Nginx Proxy Manager, GitLab, Jenkins,
and related services. Each stack lives under `stacks/<tool>/` and joins
a shared external Docker network (`edge`) so services can be
reverse-proxied by container name without exposing ports directly.

## Prerequisites

The following tools must be installed on the host before running any stack.

| Tool | Min version | Notes |
|---|---|---|
| Docker Engine | 24.0 | `docker --version` |
| Docker Compose v2 | 2.20 | Built into Docker Desktop; use `docker compose` (with space), not `docker-compose` |
| GNU Make | 3.81 | `make --version` |
| Git | 2.x | `git --version` |
| AWS CLI v2 | 2.x | Required only on hosts that run backup scripts; `aws --version` |

> Docker Compose v1 (`docker-compose` with hyphen) is not supported.
> All compose files follow the v2 spec (`compose.yaml`, no `version:` key).

## Quick Start

```bash
# 1. Clone
git clone git@github.com:focela/infra.git && cd infra

# 2. Create the shared edge network (once per host)
make network

# 3. Copy stack config and fill in values
cp stacks/proxy/.env.example stacks/proxy/.env
# edit stacks/proxy/.env

# 4. Start the stack
make up s=proxy

# 5. Verify
make ps s=proxy
```

## Repository Structure

```
infra/
├── backup/                  # Backup scripts, one per stack
│   ├── .env.example         # S3 and credential config
│   └── proxy-backup.sh
├── docs/
│   ├── architecture.md      # VM layout, network topology
│   └── standards.md         # Image pinning, naming, commit conventions
├── stacks/
│   └── proxy/               # Nginx Proxy Manager
│       ├── README.md        # Per-stack configuration and operations
│       ├── compose.yaml
│       └── .env.example
└── Makefile                 # Entry point for all stack operations
```

## Stacks

| Stack | Description | Documentation |
|---|---|---|
| `proxy` | Nginx Proxy Manager — TLS entry point and reverse proxy | [stacks/proxy/README.md](stacks/proxy/README.md) |

Each stack documents its own configuration and operations in
`stacks/<tool>/README.md`.

## Makefile Targets

```bash
make network            # Create shared edge network (run once per host)
make up s=<stack>       # Start a stack
make down s=<stack>     # Stop a stack
make restart s=<stack>  # Restart a stack
make logs s=<stack>     # Tail logs (last 100 lines)
make ps s=<stack>       # List containers
make pull s=<stack>     # Pull latest images
make backup s=<stack>   # Run backup script for a stack
```

## Backup

Each stack with persistent data has a backup script at
`backup/<stack>-backup.sh`. The script stops the container, archives the
data directory, uploads to S3-compatible storage, restarts the container,
and prunes old backups.

```bash
# Configure
cp backup/.env.example backup/.env
# Set BACKUP_S3_BUCKET and credentials — see backup/.env.example

# Test (dry run)
BACKUP_S3_BUCKET=s3://your-bucket bash backup/proxy-backup.sh --dry-run

# Run manually
make backup s=proxy
```

**Recommended cron (daily 3am):**
```
0 3 * * * cd /path/to/infra && make backup s=proxy >> /var/log/infra-backup.log 2>&1
```

Credential options: explicit AWS keys, EC2/GCP instance role, or
`~/.aws/credentials`. See `backup/.env.example` for details.

## Adding a New Stack

1. Create `stacks/<tool>/compose.yaml` — join the `edge` network
2. Create `stacks/<tool>/.env.example` — document required env vars
3. Create `stacks/<tool>/README.md` — per-stack configuration and operations
4. Create `backup/<tool>-backup.sh` if the stack has persistent data
5. Add the stack to the table under [Stacks](#stacks)
6. Open a PR following the conventions in `docs/standards.md`

## Documentation

- `docs/architecture.md` — VM layout, network topology, service placement
- `docs/standards.md` — image pinning, backup policy, naming, commit and PR conventions
