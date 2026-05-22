package com.focela.platform.datapermission.core.rule.department;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.focela.platform.common.api.system.permission.PermissionContractApi;
import com.focela.platform.common.api.system.permission.dto.DepartmentDataPermissionRpcResponse;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.utils.collection.SetUtils;
import com.focela.platform.security.core.LoginUser;
import com.focela.platform.security.core.utils.SecurityFrameworkUtils;
import com.focela.platform.test.core.support.BaseMockitoUnitTest;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Map;

import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DepartmentDataPermissionRule}.
 */
class DepartmentDataPermissionRuleTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DepartmentDataPermissionRule rule;

    @Mock
    private PermissionContractApi permissionApi;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        // Clear the rule
        rule.getTableNames().clear();
        ((Map<String, String>) ReflectUtil.getFieldValue(rule, "deptColumns")).clear();
        ((Map<String, String>) ReflectUtil.getFieldValue(rule, "deptColumns")).clear();
    }

    @Test // No LoginUser
    public void getExpression_noLoginUser() {
        // prepare parameters
        String tableName = randomString();
        Alias tableAlias = new Alias(randomString());
        // mock the method

        // invoke
        Expression expression = rule.getExpression(tableName, tableAlias);
        // assert
        assertNull(expression);
    }

    @Test // No data permission
    public void getExpression_noDepartmentDataPermission() {
        try (MockedStatic<SecurityFrameworkUtils> securityFrameworkUtilsMock
                     = mockStatic(SecurityFrameworkUtils.class)) {
            // prepare parameters
            String tableName = "t_user";
            Alias tableAlias = new Alias("u");
            // mock the method
            LoginUser loginUser = randomPojo(LoginUser.class, o -> o.setId(1L)
                    .setUserType(UserTypeEnum.ADMIN.getValue()));
            securityFrameworkUtilsMock.when(SecurityFrameworkUtils::getLoginUser).thenReturn(loginUser);
            // mock the method (permissionApi returns null)
            when(permissionApi.getDepartmentDataPermission(eq(loginUser.getId()))).thenReturn(null);

            // invoke
            NullPointerException exception = assertThrows(NullPointerException.class,
                    () -> rule.getExpression(tableName, tableAlias));
            // assert
            assertEquals("LoginUser(1) Table(t_user/u) did not return data permission", exception.getMessage());
        }
    }

    @Test // Full data permission
    public void getExpression_allDepartmentDataPermission() {
        try (MockedStatic<SecurityFrameworkUtils> securityFrameworkUtilsMock
                     = mockStatic(SecurityFrameworkUtils.class)) {
            // prepare parameters
            String tableName = "t_user";
            Alias tableAlias = new Alias("u");
            // mock the method (LoginUser)
            LoginUser loginUser = randomPojo(LoginUser.class, o -> o.setId(1L)
                    .setUserType(UserTypeEnum.ADMIN.getValue()));
            securityFrameworkUtilsMock.when(SecurityFrameworkUtils::getLoginUser).thenReturn(loginUser);
            // mock the method (DepartmentDataPermissionRpcResponse)
            DepartmentDataPermissionRpcResponse deptDataPermission = new DepartmentDataPermissionRpcResponse().setAll(true);
            when(permissionApi.getDepartmentDataPermission(same(1L))).thenReturn(deptDataPermission);

            // invoke
            Expression expression = rule.getExpression(tableName, tableAlias);
            // assert
            assertNull(expression);
            assertSame(deptDataPermission, loginUser.getContext(DepartmentDataPermissionRule.CONTEXT_KEY, DepartmentDataPermissionRpcResponse.class));
        }
    }

    @Test // Cannot view department nor self - i.e. 100% no permission
    public void getExpression_noDepartment_noSelf() {
        try (MockedStatic<SecurityFrameworkUtils> securityFrameworkUtilsMock
                     = mockStatic(SecurityFrameworkUtils.class)) {
            // prepare parameters
            String tableName = "t_user";
            Alias tableAlias = new Alias("u");
            // mock the method (LoginUser)
            LoginUser loginUser = randomPojo(LoginUser.class, o -> o.setId(1L)
                    .setUserType(UserTypeEnum.ADMIN.getValue()));
            securityFrameworkUtilsMock.when(SecurityFrameworkUtils::getLoginUser).thenReturn(loginUser);
            // mock the method (DepartmentDataPermissionRpcResponse)
            DepartmentDataPermissionRpcResponse deptDataPermission = new DepartmentDataPermissionRpcResponse();
            when(permissionApi.getDepartmentDataPermission(same(1L))).thenReturn(deptDataPermission);

            // invoke
            Expression expression = rule.getExpression(tableName, tableAlias);
            // assert
            assertEquals("null = null", expression.toString());
            assertSame(deptDataPermission, loginUser.getContext(DepartmentDataPermissionRule.CONTEXT_KEY, DepartmentDataPermissionRpcResponse.class));
        }
    }

    @Test // Combine Department and User conditions (neither column matches)
    public void getExpression_noDepartmentColumn_noSelfColumn() {
        try (MockedStatic<SecurityFrameworkUtils> securityFrameworkUtilsMock
                     = mockStatic(SecurityFrameworkUtils.class)) {
            // prepare parameters
            String tableName = "t_user";
            Alias tableAlias = new Alias("u");
            // mock the method (LoginUser)
            LoginUser loginUser = randomPojo(LoginUser.class, o -> o.setId(1L)
                    .setUserType(UserTypeEnum.ADMIN.getValue()));
            securityFrameworkUtilsMock.when(SecurityFrameworkUtils::getLoginUser).thenReturn(loginUser);
            // mock the method (DepartmentDataPermissionRpcResponse)
            DepartmentDataPermissionRpcResponse deptDataPermission = new DepartmentDataPermissionRpcResponse()
                    .setDeptIds(SetUtils.asSet(10L, 20L)).setSelf(true);
            when(permissionApi.getDepartmentDataPermission(same(1L))).thenReturn(deptDataPermission);

            // invoke
            Expression expression = rule.getExpression(tableName, tableAlias);
            // assert
            assertEquals("null = null", expression.toString());
            assertSame(deptDataPermission, loginUser.getContext(DepartmentDataPermissionRule.CONTEXT_KEY, DepartmentDataPermissionRpcResponse.class));
        }
    }

    @Test // Combine Department and User conditions (self column matches)
    public void getExpression_noDepartmentColumn_yesSelfColumn() {
        try (MockedStatic<SecurityFrameworkUtils> securityFrameworkUtilsMock
                     = mockStatic(SecurityFrameworkUtils.class)) {
            // prepare parameters
            String tableName = "t_user";
            Alias tableAlias = new Alias("u");
            // mock the method (LoginUser)
            LoginUser loginUser = randomPojo(LoginUser.class, o -> o.setId(1L)
                    .setUserType(UserTypeEnum.ADMIN.getValue()));
            securityFrameworkUtilsMock.when(SecurityFrameworkUtils::getLoginUser).thenReturn(loginUser);
            // mock the method (DepartmentDataPermissionRpcResponse)
            DepartmentDataPermissionRpcResponse deptDataPermission = new DepartmentDataPermissionRpcResponse()
                    .setSelf(true);
            when(permissionApi.getDepartmentDataPermission(same(1L))).thenReturn(deptDataPermission);
            // Add user column configuration
            rule.addUserColumn("t_user", "id");

            // invoke
            Expression expression = rule.getExpression(tableName, tableAlias);
            // assert
            assertEquals("u.id = 1", expression.toString());
            assertSame(deptDataPermission, loginUser.getContext(DepartmentDataPermissionRule.CONTEXT_KEY, DepartmentDataPermissionRpcResponse.class));
        }
    }

    @Test // Combine Department and User conditions (dept column matches)
    public void getExpression_yesDepartmentColumn_noSelfColumn() {
        try (MockedStatic<SecurityFrameworkUtils> securityFrameworkUtilsMock
                     = mockStatic(SecurityFrameworkUtils.class)) {
            // prepare parameters
            String tableName = "t_user";
            Alias tableAlias = new Alias("u");
            // mock the method (LoginUser)
            LoginUser loginUser = randomPojo(LoginUser.class, o -> o.setId(1L)
                    .setUserType(UserTypeEnum.ADMIN.getValue()));
            securityFrameworkUtilsMock.when(SecurityFrameworkUtils::getLoginUser).thenReturn(loginUser);
            // mock the method (DepartmentDataPermissionRpcResponse)
            DepartmentDataPermissionRpcResponse deptDataPermission = new DepartmentDataPermissionRpcResponse()
                    .setDeptIds(CollUtil.newLinkedHashSet(10L, 20L));
            when(permissionApi.getDepartmentDataPermission(same(1L))).thenReturn(deptDataPermission);
            // Add dept column configuration
            rule.addDeptColumn("t_user", "dept_id");

            // invoke
            Expression expression = rule.getExpression(tableName, tableAlias);
            // assert
            assertEquals("u.dept_id IN (10, 20)", expression.toString());
            assertSame(deptDataPermission, loginUser.getContext(DepartmentDataPermissionRule.CONTEXT_KEY, DepartmentDataPermissionRpcResponse.class));
        }
    }

    @Test // Combine Department and User conditions (both dept and self match)
    public void getExpression_yesDepartmentColumn_yesSelfColumn() {
        try (MockedStatic<SecurityFrameworkUtils> securityFrameworkUtilsMock
                     = mockStatic(SecurityFrameworkUtils.class)) {
            // prepare parameters
            String tableName = "t_user";
            Alias tableAlias = new Alias("u");
            // mock the method (LoginUser)
            LoginUser loginUser = randomPojo(LoginUser.class, o -> o.setId(1L)
                    .setUserType(UserTypeEnum.ADMIN.getValue()));
            securityFrameworkUtilsMock.when(SecurityFrameworkUtils::getLoginUser).thenReturn(loginUser);
            // mock the method (DepartmentDataPermissionRpcResponse)
            DepartmentDataPermissionRpcResponse deptDataPermission = new DepartmentDataPermissionRpcResponse()
                    .setDeptIds(CollUtil.newLinkedHashSet(10L, 20L)).setSelf(true);
            when(permissionApi.getDepartmentDataPermission(same(1L))).thenReturn(deptDataPermission);
            // Add user column configuration
            rule.addUserColumn("t_user", "id");
            // Add dept column configuration
            rule.addDeptColumn("t_user", "dept_id");

            // invoke
            Expression expression = rule.getExpression(tableName, tableAlias);
            // assert
            assertEquals("(u.dept_id IN (10, 20) OR u.id = 1)", expression.toString());
            assertSame(deptDataPermission, loginUser.getContext(DepartmentDataPermissionRule.CONTEXT_KEY, DepartmentDataPermissionRpcResponse.class));
        }
    }

}
