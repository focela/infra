# proxy: Nginx Proxy Manager

Nginx Proxy Manager (NPM) is the TLS entry point for all services on the
host. It issues and renews Let's Encrypt certificates, and routes HTTPS
traffic to backend services on the same host over the `edge` network.
Cross-VM upstreams use private DNS names or private IP addresses.

## Configuration

| | |
|---|---|
| Image | `jc21/nginx-proxy-manager:2.12.6` |
| Admin UI | `http://<host>:81`; restrict to trusted networks only |
| HTTP / HTTPS | `80`, `443`; expose publicly |
| Data directory | `${PROXY_DATA_DIR:-./data}` (SQLite DB, certs, nginx configs) |
| Config file | `stacks/proxy/.env` (copy from `.env.example`) |
| Compose service | `npm`; stack commands use `proxy` |

### Environment variables

| Variable | Purpose |
|---|---|
| `INITIAL_ADMIN_EMAIL` | Admin login email, set on first start |
| `INITIAL_ADMIN_PASSWORD` | Admin login password, set on first start |
| `PROXY_DATA_DIR` | Host path for NPM state; use an absolute path on production hosts |
| `DISABLE_IPV6` | Set `true` if the host has no IPv6 |

## Setup

```bash
cp stacks/proxy/.env.example stacks/proxy/.env
# Set INITIAL_ADMIN_EMAIL and INITIAL_ADMIN_PASSWORD
make up s=proxy
# Admin UI: http://<host>:81
```

Change the admin password on first login.

## Operations

```bash
make logs s=proxy      # Tail logs
make restart s=proxy   # Restart
make backup s=proxy    # Back up data/ to S3
make down s=proxy      # Stop
```

## Notes

- Port 81 (admin UI) must be blocked at the firewall. Only 80/443 are
  exposed publicly.
- Data is stored in SQLite under `PROXY_DATA_DIR`. See
  `backup/proxy-backup.sh` and `docs/standards.md` for the backup policy.
- NPM joins the host-local external `edge` network. Backend services on the
  same Docker host are reachable by container name. Backend services on other
  VMs require private DNS names or private IP addresses.
