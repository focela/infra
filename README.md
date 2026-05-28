[English](README.md) | [Tiếng Việt](README.vi.md)

[![Lint](https://github.com/focela/infra/actions/workflows/lint.yml/badge.svg)](https://github.com/focela/infra/actions/workflows/lint.yml)

Each stack lives under `stacks/<tool>/`. Services on the shared `edge` network
are reverse-proxied by container name without exposing backend ports directly.

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

# 3. Copy stack config and set values
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
│   ├── lib/
│   │   └── common.sh        # Shared backup functions
│   ├── proxy-backup.sh
│   └── wireguard-backup.sh
├── docs/
│   ├── architecture.md      # VM layout, network topology
│   └── standards.md         # Image pinning, naming, commit conventions
├── stacks/
│   ├── proxy/               # Nginx Proxy Manager
│   │   ├── README.md        # Per-stack configuration and operations
│   │   ├── compose.yaml
│   │   └── .env.example
│   └── wireguard/           # WireGuard VPN
│       ├── README.md
│       ├── compose.yaml
│       └── .env.example
└── Makefile                 # Entry point for all stack operations
```

## Stacks

| Stack | Description | Documentation |
|---|---|---|
| `proxy` | Nginx Proxy Manager: TLS entry point and reverse proxy | [stacks/proxy/README.md](stacks/proxy/README.md) |
| `wireguard` | WireGuard VPN: team access to internal services | [stacks/wireguard/README.md](stacks/wireguard/README.md) |

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
make pull s=<stack>     # Pull configured image tags
make backup s=<stack>   # Run backup script for a stack
```

## Backup

Each stack with persistent data has a backup script at
`backup/<stack>-backup.sh`. Backup scripts archive the configured data
directory, upload to S3-compatible storage, and prune old backups. Stacks with
write-sensitive local state may stop containers before archive creation; see
the per-stack backup script for the exact flow.

```bash
# Configure
cp backup/.env.example backup/.env
# Set BACKUP_S3_BUCKET and credentials; see backup/.env.example

# Test (dry run)
BACKUP_S3_BUCKET=s3://your-bucket/infra-backups bash backup/proxy-backup.sh --dry-run

# Run manually
make backup s=proxy
```

**Example cron (daily 3am):**
```
0 3 * * * cd /path/to/infra && make backup s=proxy >> /var/log/infra-backup.log 2>&1
```

Credential options: explicit AWS keys, EC2 instance profile, or
`~/.aws/credentials`. See `backup/.env.example` for details.

## Adding a New Stack

1. Create `stacks/<tool>/compose.yaml`: join `edge` unless host networking is
   required
2. Create `stacks/<tool>/.env.example`: document required env vars
3. Create `stacks/<tool>/README.md`: per-stack configuration and operations
4. Create `backup/<tool>-backup.sh` if the stack has persistent data
5. Add the stack to the table under [Stacks](#stacks)
6. Open a PR following the conventions in `docs/standards.md`

## Documentation

- `docs/architecture.md`: VM layout, network topology, service placement
- `docs/standards.md`: image pinning, backup policy, naming, commit and PR conventions
