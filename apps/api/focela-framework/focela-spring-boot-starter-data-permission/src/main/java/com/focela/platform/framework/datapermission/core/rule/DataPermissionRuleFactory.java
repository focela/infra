package com.focela.platform.framework.datapermission.core.rule;

import java.util.List;

/**
 * Factory interface for {@link DataPermissionRule}.
 * Acts as the container for {@link DataPermissionRule} instances and provides management capabilities.
 */
public interface DataPermissionRuleFactory {

    /**
     * Get all data permission rules.
     *
     * @return array of data permission rules
     */
    List<DataPermissionRule> getDataPermissionRules();

    /**
     * Get the data permission rules for the specified Mapper.
     *
     * @param mappedStatementId ID of the specified Mapper
     * @return array of data permission rules
     */
    List<DataPermissionRule> getDataPermissionRule(String mappedStatementId);

}
