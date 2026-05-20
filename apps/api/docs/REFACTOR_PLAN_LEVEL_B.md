# Refactor Plan — Mức B: Chuẩn hoá Naming Convention

> Status: **EXECUTED** (B1 + B2 + B3 hoàn tất trên `feature/SKF-1`)
> Commits: B1 `17d1d61`, B2 `80f8354`, B3 (pending push)
> Verification: `mvn test` 457 run, 456 pass (1 pre-existing timezone failure)

---

## 1. Mục tiêu

Chuẩn hoá naming convention nội bộ từ **kiểu legacy upstream** sang **kiểu Spring/Jakarta quốc tế** để:

- Cải thiện onboarding cho dev không quen convention CN
- Đồng bộ với tài liệu Spring/JPA chính thức
- Giảm rào cản khi hire/scale team
- Chuẩn hoá khi đối tác/khách hàng đọc code

**Không trong phạm vi:**

- Restructure layer (controller/service/dal vẫn giữ — đó là Mức C)
- Đổi cấu trúc bounded context
- Refactor business logic
- Đổi DB schema (column/table name giữ nguyên)

---

## 2. Phạm vi định lượng (đã khảo sát)

| Trục | Vị trí hiện tại | Số file |
|---|---|---|
| `dal/` package | 3 thư mục (2 module + 1 codegen template) | **105 file** |
| `dataobject/` package | 1 framework base + 2 module | bao gồm trong 105 |
| `vo/` package | 31 thư mục | **160 file** |
| `*DO` class suffix | Tất cả DAO entity | ~50 class |
| `*RespVO`, `*ReqVO`, `*PageRequest`, `*SaveRequest`, `*ExcelVO` | Tất cả controller DTO | ~150 class |
| MyBatis `type-aliases-package` config refs | `dal.dataobject` path | 2 YAML |

**Total impact:** ~265 file Java + 2 YAML + codegen template + cross-module reference

---

## 3. Mapping đề xuất

### 3.1 DAL layer

| Hiện tại | Đề xuất | Lý do |
|---|---|---|
| `dal/` (Data Access Layer) | `repository/` | Spring convention (`@Repository`, `JpaRepository`) |
| `dal/dataobject/` | `repository/entity/` | JPA convention (`@Entity` style) |
| `dal/mysql/` (subdir) | `repository/mapper/` | MyBatis Mapper convention |
| `dal/redis/` (subdir) | `repository/redis/` | Giữ tên tech-specific |
| `*DO` class suffix | `*Entity` hoặc bỏ suffix | JPA/Spring style |

**Lưu ý quan trọng về `*DO` suffix:**
- Option 1: `TenantDO` → `TenantEntity` (rõ ràng, dài)
- Option 2: `TenantDO` → `Tenant` (sạch, nhưng có thể trùng với DTO `Tenant*RespVO`)
- **Khuyến nghị:** Option 1 (`*Entity`) — tránh ambiguity với DTO

### 3.2 VO layer

| Hiện tại | Đề xuất | Lý do |
|---|---|---|
| `vo/` package | `dto/` package | Spring/Jakarta DTO convention |
| `*ReqVO`, `*SaveRequest`, `*PageRequest` | `*Request` | REST API standard |
| `*RespVO` | `*Response` | REST API standard |
| `*ExcelVO` | `*ExcelDto` hoặc giữ | Export Excel use case riêng |

**Phân loại chi tiết Request:**
- `*ReqVO` → `*Request` (generic request)
- `*SaveRequest` → `*SaveRequest` hoặc `*CreateRequest`/`*UpdateRequest` (tách create/update)
- `*PageRequest` → `*PageRequest` (pagination params)

**Khuyến nghị:** giữ pattern đơn giản — chỉ đổi suffix, không tách thêm:
- `*ReqVO` → `*Request`
- `*SaveRequest` → `*SaveRequest`
- `*PageRequest` → `*PageRequest`
- `*RespVO` → `*Response`
- `*ExcelVO` → `*ExcelDto`

### 3.3 Misc

| Hiện tại | Đề xuất | Note |
|---|---|---|
| `BaseDO` (framework base) | `BaseEntity` | Đồng bộ với `*Entity` |
| MyBatis config `type-aliases-package: *.dal.dataobject` | `*.repository.entity` | Phải sync với path mới |
| `enums/` | Giữ nguyên | Spring convention OK |
| `service/`, `controller/`, `api/` | Giữ nguyên | Spring convention OK |

---

## 4. Chiến lược thực hiện (phased)

### Phase B1 — DAL → Repository (rủi ro Trung bình)

**Phạm vi:** Đổi `dal/` → `repository/`, `*DO` → `*Entity`, `BaseDO` → `BaseEntity`

**Bước con:**
1. `git mv` thư mục `dal/` → `repository/`
2. `git mv` `dataobject/` → `entity/`
3. Rename file `*DO.java` → `*Entity.java`
4. Replace `class XxxDO` → `class XxxEntity`, `BaseDO` → `BaseEntity` (sed pattern)
5. Update MyBatis `type-aliases-package` trong 2 YAML
6. Update codegen template `dal/` references
7. Verify: `mvn clean test-compile` + `mvn test`

**Rủi ro cao nhất:** MyBatis type-aliases binding — nếu typo path, query fail runtime.

### Phase B2 — VO → DTO/Request/Response (rủi ro Trung bình)

**Phạm vi:** Đổi `vo/` → `dto/`, `*ReqVO`/`*RespVO`/`*PageRequest`/`*SaveRequest` → suffix mới

**Bước con:**
1. `git mv` `vo/` → `dto/`
2. Rename file pattern theo bảng mapping ở section 3.2
3. Replace class declarations + cross-module references
4. Update codegen template
5. Verify: `mvn clean test-compile` + `mvn test` + runtime smoke

**Rủi ro cao nhất:** Cross-module reference (DTO chia sẻ giữa controller các module).

### Phase B3 — Cleanup (rủi ro Thấp)

- Update codegen template documentation
- Update Javadoc reference đến tên cũ
- Update README nếu có

---

## 5. Risk Analysis

| Rủi ro | Mức | Giảm thiểu |
|---|---|---|
| MyBatis type-aliases binding sai path | **Cao** | Verify sau Phase B1 bằng `mvn test` (có DB tests) |
| Cross-module DTO reference vỡ | **Trung bình** | Compile gate sau mỗi phase |
| Codegen template lỗi thời | Thấp | Update template trong cùng phase |
| `*Entity` trùng với JPA entity của lib khác | Thấp | Không dùng JPA, chỉ MyBatis — an toàn |
| Tên class `XxxEntity` quá dài | Thấp (subjective) | Vẫn ngắn hơn `XxxDataObject` |
| Git rename detection threshold | Trung bình | Dùng `git mv`, file content thay đổi <50% sẽ giữ rename |
| Reflection/string-based class lookup | Trung bình | Grep FQN string trước khi đổi |

---

## 6. Quyết định cần bạn duyệt

| # | Quyết định | Options |
|---|---|---|
| Q1 | `*DO` đổi thành gì? | A. `*Entity` (khuyến nghị) / B. Bỏ suffix / C. Khác |
| Q2 | `*ReqVO`/`*SaveRequest` tách thành Create/Update riêng? | A. Giữ generic `*Request`/`*SaveRequest` (khuyến nghị) / B. Tách `*CreateRequest`/`*UpdateRequest` |
| Q3 | `*ExcelVO` đổi thành gì? | A. `*ExcelDto` / B. `*ExcelExport` / C. Giữ |
| Q4 | Có gộp Phase B1 + B2 thành 1 commit lớn không? | A. Tách 2 commit (khuyến nghị — dễ revert) / B. Gộp |
| Q5 | Branch chiến lược? | A. Tiếp `feature/SKF-1` (theo pattern hiện tại) / B. Tách `refactor/level-b-naming` |
| Q6 | Có rebrand luôn `BaseDO` → `BaseEntity` ở framework không? | A. Có (đồng bộ) / B. Không (chỉ đụng module, framework giữ) |

---

## 7. Effort estimate

| Phase | Effort dự kiến | Verification |
|---|---|---|
| B1 (DAL→Repository) | 30–60 phút | `mvn test` 456+/457+ pass |
| B2 (VO→DTO) | 45–90 phút | `mvn test` + runtime smoke |
| B3 (Cleanup) | 15 phút | Compile only |
| **Total** | **1.5–3 giờ** | + buffer debug |

---

## 8. Rollback strategy

- Mỗi phase = 1 commit riêng → `git revert <sha>` rollback toàn bộ phase
- Branch `feature/SKF-1` isolated → main không bị ảnh hưởng cho đến khi merge
- Greenfield (no downstream consumer) → có thể force-push branch nếu cần reset

---

## 9. Next steps

1. Bạn trả lời 6 câu hỏi Q1–Q6 ở Section 6
2. Tôi update plan này dựa trên quyết định
3. Bạn duyệt plan cuối → tôi tiến hành Phase B1
