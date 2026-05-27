# infra

[English](README.md) | [Tiếng Việt](README.vi.md)

Kho lưu trữ nội bộ quản lý hạ tầng tự triển khai tại Focela.

Quản lý các Docker Compose stack cho Nginx Proxy Manager, GitLab, Jenkins
và các dịch vụ liên quan. Mỗi stack nằm trong thư mục `stacks/<tool>/`
và kết nối vào Docker network ngoài (`edge`) để các dịch vụ có thể
giao tiếp với nhau qua tên container mà không cần expose port trực tiếp.

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

# 3. Sao chép và điền thông tin cấu hình stack
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
│   └── proxy-backup.sh
├── docs/
│   ├── architecture.md      # Sơ đồ VM, topology mạng
│   └── standards.md         # Quy chuẩn image, đặt tên, commit
├── stacks/
│   └── proxy/               # Nginx Proxy Manager
│       ├── README.md        # Cấu hình và vận hành riêng của stack
│       ├── compose.yaml
│       └── .env.example
└── Makefile                 # Entry point cho tất cả thao tác stack
```

## Danh sách Stack

| Stack | Mô tả | Tài liệu |
|---|---|---|
| `proxy` | Nginx Proxy Manager — điểm vào TLS và reverse proxy | [stacks/proxy/README.md](stacks/proxy/README.md) |

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
make pull s=<stack>     # Pull image mới nhất
make backup s=<stack>   # Chạy backup script cho stack
```

## Backup

Mỗi stack có dữ liệu lâu dài sẽ có một backup script tại
`backup/<stack>-backup.sh`. Script dừng container, nén thư mục dữ liệu,
upload lên S3-compatible storage, khởi động lại container và xóa
các bản backup cũ theo thời gian lưu trữ đã cấu hình.

```bash
# Cấu hình
cp backup/.env.example backup/.env
# Điền BACKUP_S3_BUCKET và thông tin xác thực — xem backup/.env.example

# Chạy thử (dry run)
BACKUP_S3_BUCKET=s3://your-bucket bash backup/proxy-backup.sh --dry-run

# Chạy thủ công
make backup s=proxy
```

**Cron khuyến nghị (hằng ngày lúc 3 giờ sáng):**
```
0 3 * * * cd /path/to/infra && make backup s=proxy >> /var/log/infra-backup.log 2>&1
```

Các tùy chọn xác thực: AWS key tường minh, instance role (EC2/GCP),
hoặc `~/.aws/credentials`. Xem `backup/.env.example` để biết chi tiết.

## Thêm Stack Mới

1. Tạo `stacks/<tool>/compose.yaml` — kết nối vào network `edge`
2. Tạo `stacks/<tool>/.env.example` — ghi lại các biến môi trường cần thiết
3. Tạo `stacks/<tool>/README.md` — cấu hình và vận hành riêng của stack
4. Tạo `backup/<tool>-backup.sh` nếu stack có dữ liệu lâu dài
5. Thêm stack vào bảng ở mục [Danh sách Stack](#danh-sách-stack)
6. Tạo PR theo quy chuẩn trong `docs/standards.md`

## Tài liệu

- `docs/architecture.md` — sơ đồ VM, topology mạng, vị trí triển khai từng dịch vụ
- `docs/standards.md` — quy chuẩn image, backup, đặt tên, commit và PR
