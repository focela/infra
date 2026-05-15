package com.focela.platform.datapermission.core.db;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.datapermission.core.rule.DataPermissionRule;
import com.focela.platform.datapermission.core.rule.DataPermissionRuleFactory;
import com.focela.platform.mybatis.core.utils.MyBatisUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Table;

import java.util.List;

import static com.focela.platform.security.core.utils.SecurityFrameworkUtils.skipPermissionCheck;

/**
 * Data permission handler based on {@link DataPermissionRule}.
 *
 * Built on top of MyBatis Plus's <a href="https://baomidou.com/plugins/data-permission/">data permission plugin</a>.
 * Core idea: intercept the SQL before execution and dynamically append permission-related SQL fragments
 * based on the user's permissions, so that only data the user is allowed to see gets returned.
 */
@RequiredArgsConstructor
public class DataPermissionRuleHandler implements MultiDataPermissionHandler {

    private final DataPermissionRuleFactory ruleFactory;

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // Special case: cross-tenant access
        if (skipPermissionCheck()) {
            return null;
        }

        // Get the data permission rules for the Mapper
        List<DataPermissionRule> rules = ruleFactory.getDataPermissionRule(mappedStatementId);
        if (CollUtil.isEmpty(rules)) {
            return null;
        }

        // Build the conditions
        Expression allExpression = null;
        for (DataPermissionRule rule : rules) {
            // Check whether the table name matches
            String tableName = MyBatisUtils.getTableName(table);
            if (!rule.getTableNames().contains(tableName)) {
                continue;
            }

            // Condition produced by a single rule
            Expression oneExpress = rule.getExpression(tableName, table.getAlias());
            if (oneExpress == null) {
                continue;
            }
            // Append to allExpression
            allExpression = allExpression == null ? oneExpress
                    : new AndExpression(allExpression, oneExpress);
        }
        return allExpression;
    }

}
