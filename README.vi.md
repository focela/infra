[English](README.md) | [Tiếng Việt](README.vi.md)

[![Lint](https://github.com/focela/infra/actions/workflows/lint.yml/badge.svg)](https://github.com/focela/infra/actions/workflows/lint.yml)

Mỗi stack nằm trong `stacks/<tool>/`. Các dịch vụ dùng chung network `edge`
và được reverse proxy qua tên container mà không cần expose backend port trực tiếp.

## Yêu cầu

Các công cụ sau cần được cài đặt trên host trước khi chạy bất kỳ stack nào.

| Công cụ | Phiên bản tối thiểu | Ghi chú |
|---|---|---|
| Docker Engine | 24.0 | `docker --version` |
| Docker Compose v2 | 2.20 | Tích hợp sẵn trong Docker Desktop; dùng `docker compose` (có khoảng trắng), không dùng `docker-compose` |
| GNU Make | 3.81 | `make --version` |
| Git | 2.x | `git --version` |
| AWS CLI v2 | 2.x | Chỉ cần trên host chạy backup script; `aws --version` |

> Docker Compose v1 (`docker-compose` có gạch ngang) không được hỗ trợ.
> Tất cả compose file theo chuẩn v2 (`compose.yaml`, không có khóa `version:`).

## Bắt đầu nhanh

```bash
# 1. Clone
git clone git@github.com:focela/infra.git && cd infra

# 2. Tạo shared edge network (chạy một lần trên mỗi host)
make network

# 3. Sao chép cấu hình stack
cp stacks/proxy/.env.example stacks/proxy/.env
# chỉnh sửa stacks/proxy/.env

# 4. Khởi động stack
make up s=proxy

# 5. Kiểm tra
make ps s=proxy
```

## Cấu trúc thư mục

```
infra/
├── backup/                  # Script backup, mỗi stack một file
│   ├── .env.example         # Cấu hình S3 và thông tin xác thực
│   ├── lib/
│   │   └── common.sh        # Hàm backup dùng chung
│   ├── proxy-backup.sh
│   └── wireguard-backup.sh
├── docs/
│   ├── architecture.md      # Sơ đồ VM, topology mạng
│   └── standards.md         # Quy chuẩn image, đặt tên, commit
├── stacks/
│   ├── proxy/               # Nginx Proxy Manager
│   │   ├── README.md        # Cấu hình và vận hành riêng của stack
│   │   ├── compose.yaml
│   │   └── .env.example
│   └── wireguard/           # WireGuard VPN
│       ├── README.md
│       ├── compose.yaml
│       └── .env.example
└── Makefile                 # Entry point cho tất cả thao tác stack
```

## Danh sách Stack

| Stack | Mô tả | Tài liệu |
|---|---|---|
| `proxy` | Nginx Proxy Manager: điểm vào TLS và reverse proxy | [stacks/proxy/README.md](stacks/proxy/README.md) |
| `wireguard` | WireGuard VPN: truy cập nội bộ cho team | [stacks/wireguard/README.md](stacks/wireguard/README.md) |

Mỗi stack có tài liệu cấu hình và vận hành riêng trong
`stacks/<tool>/README.md`.

## Các lệnh Makefile

```bash
make network            # Tạo shared edge network (chạy một lần trên mỗi host)
make up s=<stack>       # Khởi động stack
make down s=<stack>     # Dừng stack
make restart s=<stack>  # Khởi động lại stack
make logs s=<stack>     # Xem log (100 dòng gần nhất)
make ps s=<stack>       # Liệt kê container
make pull s=<stack>     # Pull các image tag đã cấu hình
make backup s=<stack>   # Chạy backup script cho stack
```

## Backup

Mỗi stack có dữ liệu lâu dài sẽ có một backup script tại
`backup/<stack>-backup.sh`. Backup script nén thư mục dữ liệu đã cấu hình,
upload lên S3-compatible storage và xóa các bản backup cũ theo thời gian lưu
trữ đã cấu hình. Stack có local state nhạy với ghi dữ liệu có thể dừng
container trước khi nén; xem script backup của từng stack để biết flow cụ thể.

```bash
# Cấu hình
cp backup/.env.example backup/.env
# Cấu hình BACKUP_S3_BUCKET và thông tin xác thực; xem backup/.env.example

# Chạy thử (dry run)
BACKUP_S3_BUCKET=s3://your-bucket/infra-backups bash backup/proxy-backup.sh --dry-run

# Chạy thủ công
make backup s=proxy
```

**Ví dụ cron (hằng ngày lúc 3 giờ sáng):**
```
0 3 * * * cd /path/to/infra && make backup s=proxy >> /var/log/infra-backup.log 2>&1
```

Các tùy chọn xác thực: AWS key tường minh, EC2 instance profile hoặc
`~/.aws/credentials`. Xem `backup/.env.example` để biết chi tiết.

## Thêm Stack Mới

1. Tạo `stacks/<tool>/compose.yaml`: dùng `edge` trừ khi cần host networking
2. Tạo `stacks/<tool>/.env.example`: ghi lại các biến môi trường cần thiết
3. Tạo `stacks/<tool>/README.md`: cấu hình và vận hành riêng của stack
4. Tạo `backup/<tool>-backup.sh` nếu stack có dữ liệu lâu dài
5. Thêm stack vào bảng ở mục [Danh sách Stack](#danh-sách-stack)
6. Tạo PR theo quy chuẩn trong `docs/standards.md`

## Tài liệu

- `docs/architecture.md`: sơ đồ VM, topology mạng, vị trí triển khai từng dịch vụ
- `docs/standards.md`: quy chuẩn image, backup, đặt tên, commit và PR
