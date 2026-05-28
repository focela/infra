# Architecture

## Overview

This repository manages self-hosted development tooling as isolated
Docker Compose stacks. Each tool is self-contained under `stacks/<tool>/`.
The `edge` Docker network is local to each host. Services on different VMs
use private DNS names or private IP addresses.

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

Public HTTP/HTTPS traffic enters through Nginx Proxy Manager (the `proxy`
stack), which terminates TLS and reverse-proxies to backend services.

```text
Internet
  |
  v  443 / 80
Nginx Proxy Manager (proxy stack, vm-ops)
  |
  +-- same VM: edge network by container name
  |     - Jenkins
  |     - Monitoring
  |
  +-- other VMs: VPC private DNS or private IP
        - GitLab
        - Registry

Team members (WireGuard client)
  |
  v  UDP 51820 to WireGuard VPN (10.13.13.0/24)
vm-ops WireGuard IP (10.13.13.1)
  |
  +-- port 81: NPM admin UI  (internal only, not in public SG)
  +-- port 22: SSH            (internal only, not in public SG)
```

### Shared `edge` network

- An external Docker network named `edge`, created once per Docker host with
  `make network`.
- Stacks join `edge` instead of publishing their own ports. Only the
  proxy stack publishes `80`/`443` publicly.
- Services on the same Docker host are reachable from the proxy by container
  name (e.g. `http://jenkins`).
- Services on other VMs are reached through private DNS names or private IP
  addresses. Docker container names do not resolve across hosts.

### WireGuard access

The `wireguard` stack runs with `network_mode: host`, creating a `wg0`
interface directly on the VM at `10.13.13.1`. Team members connect via the
WireGuard client app and reach internal services at `10.13.13.1:<port>`
without those ports being open in the AWS security group. Only UDP 51820
(the WireGuard handshake port) is exposed publicly.

## Stack Layout

Each stack directory is self-contained:

```
stacks/<tool>/
├── README.md        # per-stack configuration and operations
├── compose.yaml     # service definition
├── .env.example     # documented config template (committed)
├── .env             # actual config (gitignored)
└── data/            # default local data path (gitignored)
```

## Data and Backup

- Each tool owns its data. No shared database between tools.
- A tool that needs a database runs its own (bundled or a dedicated
  service inside its own stack).
- Production hosts may use absolute data paths such as
  `/srv/infra/<stack>/data` instead of storing runtime data under the source
  checkout.
- Stacks with persistent data have a backup script at
  `backup/<tool>-backup.sh` that archives the data directory to
  S3-compatible storage. See [`standards.md`](standards.md) for the
  backup policy.
