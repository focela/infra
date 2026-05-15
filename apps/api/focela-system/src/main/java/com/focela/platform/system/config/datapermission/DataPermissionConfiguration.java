package com.focela.platform.system.config.datapermission;

import com.focela.platform.system.entity.department.DepartmentEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.focela.platform.datapermission.core.rule.department.DepartmentDataPermissionRuleCustomizer;
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
            rule.addDeptColumn(UserEntity.class);
            rule.addDeptColumn(DepartmentEntity.class, "id");
            // user
            rule.addUserColumn(UserEntity.class, "id");
        };
    }

}
