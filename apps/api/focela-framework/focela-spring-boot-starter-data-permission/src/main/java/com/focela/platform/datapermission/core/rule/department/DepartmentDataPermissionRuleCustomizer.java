package com.focela.platform.datapermission.core.rule.department;

/**
 * Customizer interface for {@link DepartmentDataPermissionRule}.
 */
@FunctionalInterface
public interface DepartmentDataPermissionRuleCustomizer {

    /**
     * Customize the permission rule.
     * 1. Call {@link DepartmentDataPermissionRule#addDeptColumn(Class, String)} to configure dept_id-based filtering.
     * 2. Call {@link DepartmentDataPermissionRule#addUserColumn(Class, String)} to configure user_id-based filtering.
     *
     * @param rule the permission rule
     */
    void customize(DepartmentDataPermissionRule rule);

}
