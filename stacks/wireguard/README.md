# wireguard: Team VPN Access

WireGuard provides team access to internal services such as the Nginx Proxy
Manager admin UI on port 81 without exposing those ports publicly.

Once connected, team members reach internal services via the WireGuard VPN
IP: `http://10.13.13.1:81`.

## How it works

```text
Team member (WireGuard client)
  |
  v  UDP 51820
WireGuard server (vm-ops, 10.13.13.1)
  |
  +-- port 81: NPM admin UI
  +-- port 22: SSH
```

`ALLOWEDIPS=10.13.13.0/24` means only traffic to the VPN subnet is routed
through WireGuard. Normal internet traffic from team members is unaffected.

## Configuration

| | |
|---|---|
| Image | `lscr.io/linuxserver/wireguard:1.0.20210914` |
| Network mode | `host`; WireGuard kernel interface runs on the VM directly |
| VPN subnet | `10.13.13.0/24`; server at `10.13.13.1`, peers from `10.13.13.2` |
| Config file | `stacks/wireguard/.env` (copy from `.env.example`) |

### Environment variables

| Variable | Purpose |
|---|---|
| `WG_SERVER_URL` | Public IP or DNS of this VM; `auto` for detection |
| `WG_SERVER_PORT` | UDP listen port (default `51820`) |
| `WG_PEERS` | Number of peer configs to generate |
| `WIREGUARD_DATA_DIR` | Host path for keys and peer configs |

## Setup

### 1. Deploy the stack

```bash
cp stacks/wireguard/.env.example stacks/wireguard/.env
# Set WG_SERVER_URL to the VM's Elastic IP or DNS name
# Set WG_PEERS to the number of team members
nano stacks/wireguard/.env

make up s=wireguard
```

### 2. Distribute peer configs to team members

Each peer has a config file and a QR code generated in the data directory.

**Show QR code** (for mobile):
```bash
docker exec wireguard /app/show-peer peer1
# Scan with WireGuard mobile app
```

**Get config file** (for desktop):
```bash
docker exec wireguard cat /config/peer_peer1/peer_peer1.conf
# Send the file securely to the team member
```

Peers are named `peer1`, `peer2`, ... `peer<N>` by default.
To use named peers, set `PEERS=alice,bob,charlie` in `.env`.

### 3. Team member setup

1. Download WireGuard: [wireguard.com/install](https://www.wireguard.com/install/)
   (Windows, macOS, iOS, Android)
2. Import the `.conf` file or scan the QR code
3. Toggle **Activate**
4. Open `http://10.13.13.1:81` in a browser

## Operations

```bash
make logs s=wireguard                         # Tail logs
make restart s=wireguard                      # Restart
make backup s=wireguard                       # Back up keys and peer configs
make down s=wireguard                         # Stop
docker exec wireguard wg show                 # Show connected peers and stats
docker exec wireguard /app/show-peer peer1    # Show QR for peer1
```

**To add more peers:** increase `WG_PEERS` in `.env` and restart the stack.
Existing peer keys are preserved; only new peers are generated.

## Notes

- `network_mode: host` is required so the WireGuard kernel module creates
  `wg0` on the host. Team members reach host services at `10.13.13.1:<port>`.
- `net.ipv4.conf.all.src_valid_mark=1` cannot be set via Docker `sysctls`
  with `network_mode: host` (Docker restriction). It is set at the host level
  by `cloud-init-ops.sh` via `/etc/sysctl.d/99-wireguard.conf`.
- Peer config files contain private keys. Secure `WIREGUARD_DATA_DIR` with
  appropriate filesystem permissions. Use `backup/wireguard-backup.sh` to back
  up keys and peer configs to S3-compatible storage.
- `ALLOWEDIPS=10.13.13.0/24` enables split tunneling: only VPN subnet traffic
  is routed through WireGuard. Change to `0.0.0.0/0` to route all traffic
  through the VM (full tunnel).
