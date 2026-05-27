# Architecture

## Overview

This repository manages self-hosted development tooling as isolated
Docker Compose stacks. Each tool is self-contained under `stacks/<tool>/`
and shares a single external Docker network so services can reach each
other by container name.

## VM Layout

Services are split across two VMs by workload characteristics.

| VM | Role | Stacks |
|---|---|---|
| `vm-core` | Stateful, long-running services | GitLab, Jira, Container Registry |
| `vm-ops` | Bursty and operational services | Jenkins, Nginx Proxy Manager, Monitoring |

Rationale:
- Stateful services with heavy disk I/O are grouped on `vm-core` to keep
  backup and storage management in one place.
- CI runners and proxy/monitoring workloads on `vm-ops` can scale or
  restart without affecting stateful data.

## Network Topology

All public-facing traffic enters through Nginx Proxy Manager (the `proxy`
stack), which terminates TLS and reverse-proxies to backend services.

```
Internet
   │
   ▼  443 / 80
┌──────────────────────┐
│  Nginx Proxy Manager │   (proxy stack, vm-ops)
└──────────┬───────────┘
           │  edge network (by container name)
   ┌───────┼─────────┬───────────┐
   ▼       ▼         ▼           ▼
 GitLab  Jenkins  Registry   Monitoring
```

### Shared `edge` network

- An external Docker network named `edge`, created once per host with
  `make network`.
- Stacks join `edge` instead of publishing their own ports. Only the
  proxy stack publishes `80`/`443` publicly.
- Backend services are reachable from the proxy by container name (e.g.
  `http://gitlab`), so no backend port is exposed to the host.

## Stack Layout

Each stack directory is self-contained:

```
stacks/<tool>/
├── README.md        # per-stack configuration and operations
├── compose.yaml     # service definition; joins the edge network
├── .env.example     # documented config template (committed)
├── .env             # actual config (gitignored)
└── data/            # bind-mounted persistent data (gitignored)
```

## Data and Backup

- Each tool owns its data. No shared database between tools.
- A tool that needs a database runs its own (bundled or a dedicated
  service inside its own stack).
- Stacks with persistent data have a backup script at
  `backup/<tool>-backup.sh` that archives the data directory to
  S3-compatible storage. See [`standards.md`](standards.md) for the
  backup policy.
