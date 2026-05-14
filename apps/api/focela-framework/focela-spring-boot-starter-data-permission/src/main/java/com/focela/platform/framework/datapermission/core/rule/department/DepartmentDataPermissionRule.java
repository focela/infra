package com.focela.platform.framework.datapermission.core.rule.department;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.contract.system.permission.PermissionContractApi;
import com.focela.platform.framework.common.contract.system.permission.dto.DepartmentDataPermissionRpcResponse;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.utils.collection.CollectionUtils;
import com.focela.platform.framework.common.utils.json.JsonUtils;
import com.focela.platform.framework.datapermission.core.rule.DataPermissionRule;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.mybatis.core.utils.MyBatisUtils;
import com.focela.platform.framework.security.core.LoginUser;
import com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Department-based {@link DataPermissionRule} implementation.
 *
 * Note: when using DepartmentDataPermissionRule, the table must contain a dept_id field for the
 * department ID. The column name is customizable.
 *
 * A classic question arises in real business scenarios: when a user changes departments, should the
 * redundant dept_id be updated?
 * 1. Typically dept_id is not updated, which means the user can no longer see their previous data.
 *    (This is the approach used by the server.)
 * 2. If you want the user to still see the previous data, there are several options
 *    (you will need to modify this DepartmentDataPermissionRule implementation):
 *  1) Write a data migration script that rewrites dept_id to the new department ID (recommended).
 *      Final filter condition: WHERE dept_id = ?
 *  2) Migrating data may involve a large volume, so user_id-based filtering is an alternative;
 *     in that case you must collect all user_id values associated with the dept_id.
 *      Final filter condition: WHERE user_id IN (?, ?, ? ...)
 *  3) To preserve visibility for both the original dept_id and user_id, filter by both at once.
 *      Final filter condition: WHERE dept_id = ? OR user_id IN (?, ?, ? ...)
 */
@AllArgsConstructor
@Slf4j
public class DepartmentDataPermissionRule implements DataPermissionRule {

    /**
     * Context cache key on LoginUser.
     */
    protected static final String CONTEXT_KEY = DepartmentDataPermissionRule.class.getSimpleName();

    private static final String DEPT_COLUMN_NAME = "dept_id";
    private static final String USER_COLUMN_NAME = "user_id";

    private final PermissionContractApi permissionApi;

    /**
     * Per-table column configuration for department-based filtering.
     * Normally each table's department ID column is dept_id; this configuration allows customization.
     *
     * key: table name
     * value: column name
     */
    private final Map<String, String> deptColumns = new HashMap<>();
    /**
     * Per-table column configuration for user-based filtering.
     * Normally each table's user ID column is user_id; this configuration allows customization.
     *
     * key: table name
     * value: column name
     */
    private final Map<String, String> userColumns = new HashMap<>();
    /**
     * All table names, the union of {@link #deptColumns} and {@link #userColumns}.
     */
    private final Set<String> TABLE_NAMES = new HashSet<>();

    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }

    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // Only enforce data permission when there is a login user
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        // Only enforce data permission for admin-type users
        if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
            return null;
        }

        // Get the data permission
        DepartmentDataPermissionRpcResponse deptDataPermission = loginUser.getContext(CONTEXT_KEY, DepartmentDataPermissionRpcResponse.class);
        // If not present in the context, fetch it via the API
        if (deptDataPermission == null) {
            deptDataPermission = permissionApi.getDeptDataPermission(loginUser.getId());
            if (deptDataPermission == null) {
                log.error("[getExpression][LoginUser({}) get data permission is null]", JsonUtils.toJsonString(loginUser));
                throw new NullPointerException(String.format("LoginUser(%d) Table(%s/%s) did not return data permission",
                        loginUser.getId(), tableName, tableAlias.getName()));
            }
            // Store in the context to avoid repeated computation
            loginUser.setContext(CONTEXT_KEY, deptDataPermission);
        }

        // Case 1: ALL access — no need to append any condition
        if (deptDataPermission.getAll()) {
            return null;
        }

        // Case 2: cannot view departments nor self — i.e. 100% no permission
        if (CollUtil.isEmpty(deptDataPermission.getDeptIds())
            && Boolean.FALSE.equals(deptDataPermission.getSelf())) {
            return new EqualsTo(null, null); // WHERE null = null guarantees an empty result
        }

        // Case 3: build Department and User conditions, then combine them
        Expression deptExpression = buildDeptExpression(tableName,tableAlias, deptDataPermission.getDeptIds());
        Expression userExpression = buildUserExpression(tableName, tableAlias, deptDataPermission.getSelf(), loginUser.getId());
        if (deptExpression == null && userExpression == null) {
            // TODO: when no condition can be built, do not throw; instead return no data
            log.warn("[getExpression][LoginUser({}) Table({}/{}) DepartmentDataPermission({}) build item item is empty]",
                    JsonUtils.toJsonString(loginUser), tableName, tableAlias, JsonUtils.toJsonString(deptDataPermission));
//            throw new NullPointerException(String.format("LoginUser(%d) Table(%s/%s) build item item is empty",
//                    loginUser.getId(), tableName, tableAlias.getName()));
            return new EqualsTo(null, null); // WHERE null = null guarantees an empty result
        }
        if (deptExpression == null) {
            return userExpression;
        }
        if (userExpression == null) {
            return deptExpression;
        }
        // For now, when there is both a department list and self access, OR them together,
        // i.e. WHERE (dept_id IN ? OR user_id = ?)
        return new ParenthesedExpressionList(new OrExpression(deptExpression, userExpression));
    }

    private Expression buildDeptExpression(String tableName, Alias tableAlias, Set<Long> deptIds) {
        // If no column is configured, skip
        String columnName = deptColumns.get(tableName);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }
        // If empty, no condition
        if (CollUtil.isEmpty(deptIds)) {
            return null;
        }
        // Build the condition
        return new InExpression(MyBatisUtils.buildColumn(tableName, tableAlias, columnName),
                // Parenthesis is used to produce the () around (1,2,3)
                new ParenthesedExpressionList(new ExpressionList<LongValue>(CollectionUtils.convertList(deptIds, LongValue::new))));
    }

    private Expression buildUserExpression(String tableName, Alias tableAlias, Boolean self, Long userId) {
        // If "view self" is disabled, no condition
        if (Boolean.FALSE.equals(self)) {
            return null;
        }
        String columnName = userColumns.get(tableName);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }
        // Build the condition
        return new EqualsTo(MyBatisUtils.buildColumn(tableName, tableAlias, columnName), new LongValue(userId));
    }

    // ==================== Configuration helpers ====================

    public void addDeptColumn(Class<? extends BaseEntity> entityClass) {
        addDeptColumn(entityClass, DEPT_COLUMN_NAME);
    }

    public void addDeptColumn(Class<? extends BaseEntity> entityClass, String columnName) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
       addDeptColumn(tableName, columnName);
    }

    public void addDeptColumn(String tableName, String columnName) {
        deptColumns.put(tableName, columnName);
        TABLE_NAMES.add(tableName);
    }

    public void addUserColumn(Class<? extends BaseEntity> entityClass) {
        addUserColumn(entityClass, USER_COLUMN_NAME);
    }

    public void addUserColumn(Class<? extends BaseEntity> entityClass, String columnName) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addUserColumn(tableName, columnName);
    }

    public void addUserColumn(String tableName, String columnName) {
        userColumns.put(tableName, columnName);
        TABLE_NAMES.add(tableName);
    }

}
