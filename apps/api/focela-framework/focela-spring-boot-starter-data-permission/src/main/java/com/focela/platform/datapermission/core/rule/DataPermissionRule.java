package com.focela.platform.datapermission.core.rule;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;

import java.util.Set;

/**
 * Data permission rule interface.
 * Implement this interface to define custom data rules.
 */
public interface DataPermissionRule {

    /**
     * Return the array of table names this rule applies to.
     * Why is this needed? Data Permission rewrites SQL via WHERE clauses to return only the data
     * the user has permission to see.
     *
     * To get a table name from an entity class, call {@link TableInfoHelper#getTableInfo(Class)}.
     *
     * @return array of table names
     */
    Set<String> getTableNames();

    /**
     * Build the WHERE / OR filter condition for the given table name and alias.
     *
     * @param tableName table name
     * @param tableAlias alias; may be null
     * @return the filter Expression
     */
    Expression getExpression(String tableName, Alias tableAlias);

}
