package com.focela.platform.datapermission.core.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.focela.platform.datapermission.core.annotation.DataPermission;
import com.focela.platform.datapermission.core.aop.DataPermissionContextHolder;
import com.fhs.trans.service.impl.SimpleTransService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default DefaultDataPermissionRuleFactory implementation.
 * Supports filtering data permission via {@link DataPermissionContextHolder}.
 */
@RequiredArgsConstructor
public class DefaultDataPermissionRuleFactory implements DataPermissionRuleFactory {

    /**
     * Data permission rules
     */
    private final List<DataPermissionRule> rules;

    @Override
    public List<DataPermissionRule> getDataPermissionRules() {
        return rules;
    }

    @Override // The mappedStatementId parameter is currently unused. In the future we may cache based on mappedStatementId + DataPermission.
    public List<DataPermissionRule> getDataPermissionRule(String mappedStatementId) {
        // 1.1 No data permission rules
        if (CollUtil.isEmpty(rules)) {
            return Collections.emptyList();
        }
        // 1.2 Not configured: enabled by default
        DataPermission dataPermission = DataPermissionContextHolder.get();
        if (dataPermission == null) {
            return rules;
        }
        // 1.3 Configured but disabled
        if (!dataPermission.enable()) {
            return Collections.emptyList();
        }
        // 1.4 Special case: forcibly ignore data permission during data translation
        // https://github.com/YunaiV/ruoyi-vue-pro/issues/1007
        if (isTranslateCall()) {
            return Collections.emptyList();
        }

        // 2.1 Case one: configured, select only certain rules
        if (ArrayUtil.isNotEmpty(dataPermission.includeRules())) {
            return rules.stream().filter(rule -> ArrayUtil.contains(dataPermission.includeRules(), rule.getClass()))
                    .collect(Collectors.toList()); // Rules are usually few, so a HashSet lookup isn't worth it
        }
        // 2.2 Configured, exclude certain rules
        if (ArrayUtil.isNotEmpty(dataPermission.excludeRules())) {
            return rules.stream().filter(rule -> !ArrayUtil.contains(dataPermission.excludeRules(), rule.getClass()))
                    .collect(Collectors.toList()); // Rules are usually few, so a HashSet lookup isn't worth it
        }
        // 2.3 Configured, all rules apply
        return rules;
    }

    /**
     * Check whether this is a data translation call via {@link com.fhs.core.trans.anno.Trans}.
     *
     * This is currently the only available approach; we have discussed it with easy-trans.
     *
     * @return whether it is a translation call
     */
    private boolean isTranslateCall() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stack) {
            if (SimpleTransService.class.getName().equals(e.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
