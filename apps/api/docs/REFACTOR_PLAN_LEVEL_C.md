# Refactor Plan — Mức C: Structural Refactor

> Status: **EXECUTED** (C1 + C2 hoàn tất trên `feature/SKF-1`)
> Verification: `mvn clean test` BUILD SUCCESS toàn reactor
> Original RFC retained below for context.

---

## 1. Mục đích

Mức B đã chuẩn hoá *naming convention* (entity ngôn ngữ, suffix `*Request`/`*Response`/`*Entity`/`*Repository`). RFC này đề xuất bước tiếp theo: chuẩn hoá *package layout* để khớp gần hơn với layout phổ biến trong Spring/JPA enterprise codebases lớn (Pivotal samples, Spring Boot Starter project, Spotify backend, etc.).

**Trong phạm vi:**
- C1. Đổi package `entity/` → `domain/entity/`
- C2. Tách package `controller/.../dto/` thành `controller/.../request/` và `controller/.../response/`

**Không trong phạm vi:**
- Tách bounded context khác (vẫn 1 module `focela-system` / `focela-infra`)
- Thay đổi business logic hoặc DB schema
- Sửa cross-module API (`focela-common/api/.../dto/` giữ nguyên — đã thống nhất `*RpcRequest`/`*RpcResponse`)

---

## 2. Hiện trạng định lượng (đo bằng `find`)

| Trục | Path hiện tại | Số file |
|---|---|---|
| Entity classes | `*/entity/**/*Entity.java` | 43 |
| Controller DTO classes | `*/controller/**/dto/*.java` | 135 |
| — `*Request` | 76 | |
| — `*Response` | 58 | |
| — `*ExcelRow` | 1 | |
| Controller DTO sub-packages | `*/controller/**/dto/` | 20 (admin + app, có 2 nested: `dto/packages`, `dto/template`, `dto/message`) |

Module bị ảnh hưởng chính: `focela-system` (đa số) + `focela-infra` (vài file).

---

## 3. Đề xuất C1 — `entity/` → `domain/entity/`

### 3.1. Pattern enterprise tham chiếu

```
com.example.system
├── domain
│   ├── entity        // @TableName / @Entity classes
│   └── enums         // (nếu có domain enums tách riêng)
├── repository
├── service
└── controller
```

Lý do `domain/` bao bọc:
- Tách rõ "domain model" (entity + value objects + domain enums + domain events) với "data access" (`repository/`) và "delivery" (`controller/`).
- Khớp với DDD-style và Spring Modulith convention.
- Khi sau này muốn split module theo bounded context, `domain/` là root tự nhiên.

### 3.2. Hiện trạng

```
com.focela.platform.system.entity.user.UserEntity
com.focela.platform.system.entity.department.DepartmentEntity
...
```

### 3.3. Sau refactor

```
com.focela.platform.system.domain.entity.user.UserEntity
com.focela.platform.system.domain.entity.department.DepartmentEntity
...
```

### 3.4. Blast radius

- **43 file** cần `git mv`.
- **import statements** trong: controller, service, repository, mapper, test → ước tính **~250 file** chạm import.
- **MyBatis-Plus**: `@TableName` annotation không phụ thuộc package, không ảnh hưởng SQL/DB.
- **ArchUnit rules** ([ArchitectureRules.java:47-50](../focela-framework/focela-spring-boot-starter-test/src/main/java/com/focela/platform/test/core/arch/ArchitectureRules.java#L47)) cần update:
  ```
  // Before
  .should().resideInAPackage("..entity..")
  // After
  .should().resideInAPackage("..domain.entity..")
  ```
- **MyBatis XML mappers**: `<select resultType="...UserEntity">` dùng full class path → cần sed.
- **PageHelper / data-permission rules**: scan annotation `@TableId`, `@TableName` — không phụ thuộc package.

### 3.5. Migration strategy

```
1. Tạo package `domain/entity/<sub>` song song
2. git mv mỗi file (giữ history)
3. sed -i replace package declarations
4. sed -i replace imports across codebase
5. Update ArchUnit rule
6. Update MyBatis XML resultType nếu có
7. mvn clean test
```

Script template:
```bash
# Move entity files
for f in $(find focela-system/src/main/java -path '*/entity/*' -name '*.java'); do
  new=$(echo "$f" | sed 's|/entity/|/domain/entity/|')
  mkdir -p "$(dirname "$new")"
  git mv "$f" "$new"
done

# Rewrite package declarations
find focela-system/src/main/java/com/focela/platform/system/domain/entity -name '*.java' \
  | xargs sed -i '' 's|package com.focela.platform.system.entity\.|package com.focela.platform.system.domain.entity.|'

# Rewrite imports
grep -rl 'com.focela.platform.system.entity\.' --include='*.java' \
  | xargs sed -i '' 's|com.focela.platform.system.entity\.|com.focela.platform.system.domain.entity.|g'

# Update ArchUnit
sed -i '' 's|"..entity.."|"..domain.entity.."|' \
  focela-framework/focela-spring-boot-starter-test/.../ArchitectureRules.java
```

### 3.6. Risk / Cost

| Item | Đánh giá |
|---|---|
| Logic risk | Zero — package change only |
| Build risk | Trung bình — phải catch hết imports |
| PR size | ~300 file (rename + import update) |
| Review burden | Cao — diff lớn nhưng đồng nhất |
| Rollback cost | Thấp — revert PR |

### 3.7. Khuyến nghị

**DEFER** cho đến khi có nhu cầu split bounded context. Lý do:
- Refactor "đẹp" nhưng không gỡ blocker nào cụ thể.
- PR size lớn dễ conflict với branch khác đang work-in-progress.
- Có thể làm sau khi codebase tăng quy mô bounded contexts > 3.

---

## 4. Đề xuất C2 — Tách `controller/.../dto/` thành `request/` + `response/`

### 4.1. Pattern enterprise tham chiếu

```
com.example.user.controller.admin
├── UserController
├── request
│   ├── UserCreateRequest
│   ├── UserUpdateRequest
│   └── UserPageRequest
└── response
    ├── UserResponse
    └── UserExportResponse
```

Lý do tách:
- Khi tìm "tất cả input của controller X" → mở thẳng `request/`.
- Khi gen client SDK / OpenAPI → tách input/output rõ ràng.
- Một số quy chuẩn (Google Java Style, Spotify backend conventions) dùng layout này.

### 4.2. Hiện trạng

```
com.focela.platform.system.controller.admin.user.dto.UserSaveRequest
com.focela.platform.system.controller.admin.user.dto.UserPageRequest
com.focela.platform.system.controller.admin.user.dto.UserResponse
com.focela.platform.system.controller.admin.user.dto.UserImportExcelRow
```

### 4.3. Sau refactor

```
com.focela.platform.system.controller.admin.user.request.UserSaveRequest
com.focela.platform.system.controller.admin.user.request.UserPageRequest
com.focela.platform.system.controller.admin.user.response.UserResponse
com.focela.platform.system.controller.admin.user.request.UserImportExcelRow  // import payload
```

(`UserImportExcelRow` về `request/` vì là Excel input.)

### 4.4. Blast radius

- **135 file** cần `git mv` (76 Request + 58 Response + 1 ExcelRow).
- **Nested DTO directories**: `dto/packages`, `dto/template`, `dto/message`, `dto/menu`, `dto/profile` → cần map kỹ:
  - `dto/packages/*Request.java` → `request/packages/`
  - `dto/menu/*` → tách theo type
- **Imports trong controllers, services, tests**: ~150 file.
- **ArchUnit rule** ([ArchitectureRules.java:114-121](../focela-framework/focela-spring-boot-starter-test/src/main/java/com/focela/platform/test/core/arch/ArchitectureRules.java#L114)) — đổi:
  ```
  // Before
  .resideInAPackage("..controller..dto..")
  // After
  .resideInAnyPackage("..controller..request..", "..controller..response..")
  ```
- **SERVICE_DOES_NOT_USE_CONTROLLER / REPOSITORY_DOES_NOT_USE_CONTROLLER** rules ([ArchitectureRules.java:94-105](../focela-framework/focela-spring-boot-starter-test/src/main/java/com/focela/platform/test/core/arch/ArchitectureRules.java#L94)) — predicate `IN_CONTROLLER_NOT_DTO` cần update tương tự (exclude `request`/`response` sub-packages thay vì `dto`).
- **OpenAPI / Swagger**: schema path tham chiếu qua class FQN, sẽ thay đổi nếu đang export OpenAPI spec.

### 4.5. Migration strategy

```
1. Map từng file Request|Response|ExcelRow → request/ hoặc response/
2. git mv với split logic (sub-package preserve)
3. sed package declarations
4. sed imports
5. Update ArchUnit rules (2 rule)
6. mvn clean test
```

Script template:
```bash
for f in $(find . -path '*controller*dto*' -name '*Request.java' -not -path '*/test/*'); do
  new=$(echo "$f" | sed 's|/dto/|/request/|')
  mkdir -p "$(dirname "$new")"; git mv "$f" "$new"
done
for f in $(find . -path '*controller*dto*' -name '*Response.java' -not -path '*/test/*'); do
  new=$(echo "$f" | sed 's|/dto/|/response/|')
  mkdir -p "$(dirname "$new")"; git mv "$f" "$new"
done
# Then: ExcelRow → request/ (manual case)
# Rewrite package + imports
find . -name '*.java' -exec grep -l 'controller\..*\.dto\.' {} \; \
  | xargs sed -i '' -E 's|(controller\.[^.]+(\.[^.]+)?)\.dto\.([A-Z][A-Za-z]+(Request|ExcelRow))\b|\1.request.\3|g; s|(controller\.[^.]+(\.[^.]+)?)\.dto\.([A-Z][A-Za-z]+Response)\b|\1.response.\3|g'
```

### 4.6. Risk / Cost

| Item | Đánh giá |
|---|---|
| Logic risk | Zero — package change only |
| Build risk | Trung bình-Cao — regex split phức tạp hơn C1 vì 2 đích khác nhau, nested packages |
| PR size | ~285 file (135 rename + 150 import update) |
| Review burden | Cao |
| Rollback cost | Thấp |
| Tooling risk | Trung bình — OpenAPI/Swagger schema URLs change |

### 4.7. Khuyến nghị

**DEFER** trừ khi có driver cụ thể như:
- Cần export OpenAPI spec cho external SDK gen → tách helpful.
- Có nhiều người dev mới phàn nàn về việc tìm input/output.
- Đang restructure thư mục cho lý do khác (gộp với C1).

Hiện tại `dto/` đang là 1 package phẳng và cũng acceptable enterprise pattern (Pivotal Spring PetClinic, Spring Boot Sample apps đều dùng `dto/` hoặc `payload/`). Không phải hard requirement.

---

## 5. Kết hợp C1 + C2 thành 1 PR (option)

Nếu approve cả 2:
- **PR size**: ~580 file
- **Lợi ích**: 1 lần migration, không 2 đợt churn
- **Rủi ro**: PR khổng lồ, review khó. Nên split 2 PR riêng C1 trước, C2 sau.

---

## 6. Câu hỏi cho reviewer

1. Có driver cụ thể nào (OpenAPI export, team feedback) đẩy structural refactor này lên priority không?
2. Nếu defer, định kỳ tái-evaluate khi nào (Q3/Q4 next year)?
3. ArchUnit có nên thêm rule strict `*Request must reside in request/` ngay cả khi không thực hiện C2 (để future-proof)?

---

## 7. Decision log

- 2026-05-19: RFC drafted. Status: PROPOSED, không execute.
