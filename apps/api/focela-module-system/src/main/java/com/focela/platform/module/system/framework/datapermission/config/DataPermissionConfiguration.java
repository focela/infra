package com.focela.platform.module.system.framework.datapermission.config;

import com.focela.platform.module.system.repository.entity.department.DepartmentEntity;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;
import com.focela.platform.framework.datapermission.core.rule.department.DepartmentDataPermissionRuleCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * system 模块的数据权限 Configuration
 */
@Configuration(proxyBeanMethods = false)
public class DataPermissionConfiguration {

    @Bean
    public DepartmentDataPermissionRuleCustomizer sysDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(AdminUserEntity.class);
            rule.addDeptColumn(DepartmentEntity.class, "id");
            // user
            rule.addUserColumn(AdminUserEntity.class, "id");
        };
    }

}
