package com.focela.platform.framework.datapermission.core.rule.department;

/**
 * {@link DepartmentDataPermissionRule} 的自定义配置接口
 *
 * @author 芋道源码
 */
@FunctionalInterface
public interface DepartmentDataPermissionRuleCustomizer {

    /**
     * 自定义该权限规则
     * 1. 调用 {@link DepartmentDataPermissionRule#addDeptColumn(Class, String)} 方法，配置基于 dept_id 的过滤规则
     * 2. 调用 {@link DepartmentDataPermissionRule#addUserColumn(Class, String)} 方法，配置基于 user_id 的过滤规则
     *
     * @param rule 权限规则
     */
    void customize(DepartmentDataPermissionRule rule);

}
