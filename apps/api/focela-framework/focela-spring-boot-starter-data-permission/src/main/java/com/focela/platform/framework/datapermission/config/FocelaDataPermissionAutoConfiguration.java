package com.focela.platform.framework.datapermission.config;

import com.focela.platform.framework.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import com.focela.platform.framework.datapermission.core.db.DataPermissionRuleHandler;
import com.focela.platform.framework.datapermission.core.rule.DataPermissionRule;
import com.focela.platform.framework.datapermission.core.rule.DataPermissionRuleFactory;
import com.focela.platform.framework.datapermission.core.rule.DefaultDataPermissionRuleFactory;
import com.focela.platform.framework.mybatis.core.utils.MyBatisUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Auto-configuration for data permission.
 */
@AutoConfiguration
public class FocelaDataPermissionAutoConfiguration {

    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(List<DataPermissionRule> rules) {
        return new DefaultDataPermissionRuleFactory(rules);
    }

    @Bean
    public DataPermissionRuleHandler dataPermissionRuleHandler(MybatisPlusInterceptor interceptor,
                                                               DataPermissionRuleFactory ruleFactory) {
        // Create the DataPermissionInterceptor
        DataPermissionRuleHandler handler = new DataPermissionRuleHandler(ruleFactory);
        DataPermissionInterceptor inner = new DataPermissionInterceptor(handler);
        // Add to the interceptor chain.
        // Must be added at the head, before the pagination plugin. This is MyBatis Plus's rule.
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return handler;
    }

    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }

}
