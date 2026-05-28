# Terraform

Defines the Terraform-managed AWS resources for this repository.
Application stacks live in `stacks/`.

---

## Prerequisites / Yêu cầu

| Tool | Version |
|---|---|
| Terraform | ≥ 1.6 |
| AWS CLI | v2 |
| AWS credentials | configured via env vars or `~/.aws/credentials` |

---

## One-time Bootstrap / Khởi tạo lần đầu

The Terraform state is stored in S3 with DynamoDB locking.
Create these two resources manually **once** before running `terraform init`.

Trạng thái Terraform lưu trên S3, khóa bằng DynamoDB.
Tạo hai resource này thủ công **một lần duy nhất** trước khi chạy `terraform init`.

```bash
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REGION=ap-southeast-1

# 1. State bucket
aws s3 mb "s3://infra-tfstate-${ACCOUNT_ID}" --region "${REGION}"
aws s3api put-bucket-versioning \
  --bucket "infra-tfstate-${ACCOUNT_ID}" \
  --versioning-configuration Status=Enabled
aws s3api put-bucket-encryption \
  --bucket "infra-tfstate-${ACCOUNT_ID}" \
  --server-side-encryption-configuration \
  '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'

# 2. DynamoDB lock table
aws dynamodb create-table \
  --table-name infra-tfstate-lock \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region "${REGION}"
```

Then create your local backend config (gitignored):

Sau đó tạo file backend config local (đã gitignore):

```bash
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
cp terraform/envs/prod/backend.hcl.example terraform/envs/prod/backend.hcl
# sed -i.bak works on both macOS and Linux
sed -i.bak "s/YOUR_ACCOUNT_ID/${ACCOUNT_ID}/" terraform/envs/prod/backend.hcl \
  && rm terraform/envs/prod/backend.hcl.bak

cp terraform/envs/prod/terraform.tfvars.example terraform/envs/prod/terraform.tfvars
# Edit terraform.tfvars if this environment needs non-default values.
```

---

## First Deploy / Lần đầu triển khai

```bash
make tf-init e=prod   # loads backend.hcl, downloads providers
make tf-plan e=prod   # review the execution plan
make tf-apply e=prod  # apply; type "yes" to confirm
```

After apply, note the outputs:

```
vm_ops_public_ip    = "x.x.x.x"   # update DNS / firewall rules
vm_ops_instance_id  = "i-..."      # used for SSM sessions
backup_bucket       = "infra-backups-ACCOUNT_ID"
```

Update `backup/.env` with the backup bucket name:

```
BACKUP_S3_BUCKET=s3://infra-backups-ACCOUNT_ID/infra-backups
```

---

## Deploy stacks after apply / Triển khai stack sau khi apply

Terraform provisions the VM and installs Docker via cloud-init. Stack
deployment (Nginx Proxy Manager and future stacks) is a manual step because
it requires secrets that must not appear in Terraform state.

Terraform tạo VM và cài Docker qua cloud-init. Triển khai stack là bước
thủ công vì cần secrets không được lưu trong Terraform state.

### 1. Wait for cloud-init / Chờ cloud-init hoàn tất (~3–5 phút)

```bash
export AWS_PROFILE=prod   # or the profile that has access to this environment
INSTANCE_ID=$(terraform -chdir=terraform/envs/prod output -raw vm_ops_instance_id)

# Poll until Docker is available on the instance
aws ssm send-command \
  --instance-ids "${INSTANCE_ID}" \
  --document-name "AWS-RunShellScript" \
  --parameters 'commands=["docker info > /dev/null && echo ready"]' \
  --region ap-southeast-1 \
  --query "Command.CommandId" --output text
```

### 2. Connect to the VM / Kết nối vào VM

```bash
aws ssm start-session --target "${INSTANCE_ID}" --region ap-southeast-1
```

### 3. Clone the repo and deploy the proxy stack / Clone repo và deploy proxy stack

Run the following commands inside the SSM session:

Chạy các lệnh sau bên trong SSM session:

```bash
git clone https://github.com/focela/infra.git
cd infra

# Configure the proxy stack
cp stacks/proxy/.env.example stacks/proxy/.env
# Edit .env; set INITIAL_ADMIN_EMAIL and INITIAL_ADMIN_PASSWORD
nano stacks/proxy/.env

make up s=proxy

# Verify the container is running
make ps s=proxy
```

### 4. Access the NPM admin UI / Truy cập NPM admin UI

Port 81 is closed on the security group. Use SSM port forwarding to
reach it from your local machine.

Cổng 81 đóng trên security group. Dùng SSM port forwarding để truy cập
từ máy local.

```bash
# Run in a separate terminal on your local machine
AWS_PROFILE=prod aws ssm start-session \
  --target "${INSTANCE_ID}" \
  --document-name AWS-StartPortForwardingSession \
  --parameters '{"portNumber":["81"],"localPortNumber":["8081"]}' \
  --region ap-southeast-1
```

Then open `http://localhost:8081` in a browser.

Sau đó mở `http://localhost:8081` trên trình duyệt.

Initial credentials on first login / Thông tin đăng nhập lần đầu:

```
Email:    value of INITIAL_ADMIN_EMAIL in stacks/proxy/.env
Password: value of INITIAL_ADMIN_PASSWORD in stacks/proxy/.env
```

Change the admin password on first login.

### 5. Configure backup / Cấu hình backup

```bash
cp backup/.env.example backup/.env
# Edit backup/.env; set BACKUP_S3_BUCKET to the value from terraform output
# backup_bucket = "infra-backups-ACCOUNT_ID"
nano backup/.env
```

---

## Connecting via SSM / Kết nối qua SSM

Port 22 is closed. Use AWS Systems Manager Session Manager.

Cổng 22 đóng. Dùng AWS Systems Manager Session Manager.

```bash
# Interactive shell
aws ssm start-session --target i-... --region ap-southeast-1

# SSH over SSM tunnel; requires a local ProxyCommand configuration.
ssh vm-ops
```

---

## Day-to-day / Sử dụng hàng ngày

```bash
make tf-plan e=prod    # preview changes
make tf-apply e=prod   # apply changes
make tf-destroy e=prod # destroy Terraform-managed resources
```

---

## Layout / Cấu trúc

```
terraform/
├── modules/
│   └── compute/       # reusable EC2 + EIP module
└── envs/
    └── prod/
        ├── providers.tf          # partial backend + provider config
        ├── backend.hcl.example   # copy to backend.hcl and set values
        ├── backend.hcl           # gitignored backend config
        ├── variables.tf
        ├── terraform.tfvars.example
        ├── terraform.tfvars        # gitignored local values
        ├── network.tf            # shared: VPC, subnets, IGW, NAT, S3 endpoint
        ├── iam.tf                # shared: IAM roles + instance profiles
        ├── storage.tf            # shared: S3 backup bucket
        ├── vm-ops.tf             # vm-ops: security group + EC2 module call
        ├── vm-core.tf            # add with vm-core when needed
        ├── outputs.tf
        └── files/
            ├── cloud-init-ops.sh   # vm-ops first-boot: Docker, edge network, AWS CLI
            └── cloud-init-core.sh  # add with vm-core when needed
```
