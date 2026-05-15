# Chinese → English Translation Glossary

> Shared reference for translating Chinese comments/strings in the codebase.
> Apply consistently across all modules to avoid divergence.

## Domain terms (must match exactly)

| 中文 | English | Notes |
|---|---|---|
| 用户 | user | |
| 管理员 / 后台用户 | admin user | |
| 会员 | member | distinct from admin user |
| 租户 | tenant | multi-tenancy |
| 角色 | role | |
| 权限 | permission | |
| 菜单 | menu | |
| 部门 | department | |
| 岗位 | post | |
| 字典 | dictionary | |
| 字典类型 | dictionary type | |
| 字典数据 | dictionary data | |
| 短信 | SMS | |
| 邮件 / 邮箱 | email / mail | |
| 通知 | notification / notify | |
| 公告 | notice | |
| 操作日志 | operate log | |
| 访问日志 | access log | |
| 错误日志 | error log | |
| 登录日志 | login log | |
| 访问令牌 | access token | |
| 刷新令牌 | refresh token | |
| 客户端 | client | |
| 授权码 | authorization code | |
| 授权范围 | authorization scope / scope | |
| 验证码 | captcha / verification code | captcha for image; verification code for SMS |
| 手机号 / 手机号码 | mobile / mobile number | |
| 配置 | config / configuration | |
| 接口 | interface | |
| 实现类 | implementation class | |
| 编号 / ID | ID | use ID, not "number" |

## Action verbs

| 中文 | English |
|---|---|
| 创建 | create |
| 更新 / 修改 | update |
| 删除 | delete |
| 查询 / 获取 | get / query |
| 校验 / 验证 | validate / verify |
| 发送 | send |
| 调用 | call / invoke |
| 配置 (动词) | configure |
| 初始化 | initialize |
| 注册 | register |
| 登录 | login / sign in |
| 注销 / 登出 | logout / sign out |
| 绑定 | bind |
| 解绑 | unbind |

## Test-related (very common in test files)

| 中文 | English |
|---|---|
| 准备参数 | prepare parameters |
| 调用 (in tests) | invoke |
| 断言 | assert |
| 断言调用 | assert call |
| 校验记录的属性是否正确 | verify record properties are correct |
| 校验是否更新正确 | verify update is correct |
| 校验数据不存在了 | verify data no longer exists |
| 校验异常 | verify exception |
| 测试 / 单元测试 | test / unit test |
| 单元测试类 | unit test class |
| 不匹配 | mismatch |
| 数据 / 数据库 | data / database |
| 方法 | method |
| 实现类 | implementation class |
| 随机一个 | a random |
| mock 方法 | mock the method |
| 先插入出一条存在的数据 | first insert an existing record |
| 等会查询到 | will be queried later |
| 测试用例 | test case |
| 准备 mock | prepare mock |

## Common phrases

| 中文 | English |
|---|---|
| 不能为空 | must not be blank / required |
| 已存在 | already exists |
| 不存在 | does not exist |
| 失败 | failed |
| 成功 | succeeded |
| 默认 | default |
| 自定义 | custom |
| 备注 | remarks / notes |
| 类型 | type |
| 参数 | parameter / param |
| 原因 | reason / cause |
| 状态 | status |
| 范围 | range / scope |
| 列表 | list |
| 分页 | page / pagination |
| 排序 | sort |
| 关联 | associated / linked |
| 冗余 | redundant |
| 等数据库 | databases such as / for databases like |
| 数据库的主键自增 | database primary key auto-increment |
| 可不写 | can be omitted |
| 防止 | prevent |
| 被屏蔽 | blocked / suppressed |
| 操作为 | operation is |
| 建议后续修复 | follow-up fix recommended |
| 避免构建报错 | avoid build errors |
| 无需断言 | no assertion needed |
| 基于 | based on |
| 使用 | use |
| 获取最新的 | get the latest |
| 您的验证码为 | Your verification code is |
| 您的验证码是 | Your verification code is |
| 短信验证码 | SMS verification code |
| 短信发送 | SMS sending |

## Drop entirely (yudao project markers)

| Token | Action |
|---|---|
| 芋道 / 芋艿 (yudao project name / author) | DROP — replace with Focela or remove sentence |
| 芋道源码 | DROP |

## yudao `*X` extension-class convention (DO NOT rename)

Several base classes under `focela-spring-boot-starter-mybatis` carry the `X`
suffix — a yudao convention meaning *"eXtended version of the upstream
MyBatis-Plus class with project-specific helpers"*. They are kept as-is to
preserve compatibility with ~40 mapper classes (and counting) that extend
them. International readers should treat the `X` as `Ext`.

| Class | Extends | Purpose |
|---|---|---|
| `BaseMapperX<T>` | `com.baomidou.mybatisplus.core.mapper.BaseMapper<T>` | Adds joined-table page selects, batch upsert, null-safe `selectList(LambdaQueryWrapperX)` |
| `QueryWrapperX<T>` | `com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>` | Null-safe `eq/in/like/between/likeIfPresent` overrides |
| `LambdaQueryWrapperX<T>` | `com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<T>` | Lambda-style null-safe wrapper |
| `MPJLambdaWrapperX<T>` | `com.github.yulichang.wrapper.MPJLambdaWrapper<T>` | mybatis-plus-join lambda wrapper extension |

**Rename rationale (skipped):** each rename would cascade through ~40 callers
per class and bring no functional improvement. The suffix is documented here
so it is not treated as an unintentional naming oddity during onboarding.

## Rules

1. **Do NOT translate** Java code: class names, method names, variable names, package names, annotation values that are technical identifiers.
2. **Do translate**:
   - Javadoc comments `/** ... */`
   - Inline comments `// ...` and `/* ... */`
   - String literals that are human-readable messages (error messages, log messages, Swagger descriptions)
   - `package-info.java` content
3. **Do NOT change behavior**: only language. Same logic, same control flow.
4. **String literal caveat**: if a string is used as a key, lookup value, or compared elsewhere, leave it. Only translate display strings (messages shown to humans).
5. **When unsure**: leave the original Chinese and flag with `// TODO: translate` for human review.
6. **Consistency**: prefer terms from this glossary over your own translation.
