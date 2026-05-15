package com.focela.platform.framework.datapermission.config;

import com.focela.platform.framework.common.api.system.permission.PermissionContractApi;
import com.focela.platform.framework.datapermission.core.rule.department.DepartmentDataPermissionRule;
import com.focela.platform.framework.datapermission.core.rule.department.DepartmentDataPermissionRuleCustomizer;
import com.focela.platform.framework.security.core.LoginUser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Auto-configuration for department-based data permission.
 */
@AutoConfiguration
@ConditionalOnClass(LoginUser.class)
@ConditionalOnBean(value = {DepartmentDataPermissionRuleCustomizer.class})
public class FocelaDepartmentDataPermissionAutoConfiguration {

    @Bean
    public DepartmentDataPermissionRule deptDataPermissionRule(PermissionContractApi permissionApi,
                                                         List<DepartmentDataPermissionRuleCustomizer> customizers) {
        // Create the DepartmentDataPermissionRule
        DepartmentDataPermissionRule rule = new DepartmentDataPermissionRule(permissionApi);
        // Complete table configuration
        customizers.forEach(customizer -> customizer.customize(rule));
        return rule;
    }

}
