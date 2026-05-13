package com.focela.platform.framework.datapermission.config;

import com.focela.platform.framework.common.contract.system.permission.PermissionContractApi;
import com.focela.platform.framework.datapermission.core.rule.department.DepartmentDataPermissionRule;
import com.focela.platform.framework.datapermission.core.rule.department.DepartmentDataPermissionRuleCustomizer;
import com.focela.platform.framework.security.core.LoginUser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 基于部门的数据权限 AutoConfiguration
 */
@AutoConfiguration
@ConditionalOnClass(LoginUser.class)
@ConditionalOnBean(value = {DepartmentDataPermissionRuleCustomizer.class})
public class FocelaDepartmentDataPermissionAutoConfiguration {

    @Bean
    public DepartmentDataPermissionRule deptDataPermissionRule(PermissionContractApi permissionApi,
                                                         List<DepartmentDataPermissionRuleCustomizer> customizers) {
        // 创建 DepartmentDataPermissionRule 对象
        DepartmentDataPermissionRule rule = new DepartmentDataPermissionRule(permissionApi);
        // 补全表配置
        customizers.forEach(customizer -> customizer.customize(rule));
        return rule;
    }

}
