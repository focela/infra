package com.focela.platform.tenant.core.db;

import com.focela.platform.tenant.config.TenantProperties;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.tenant.core.context.TenantContextHolder;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements multi-tenancy at the DB layer based on MyBatis Plus multi-tenant feature.
 */
public class TenantDatabaseInterceptor implements TenantLineHandler {

    /**
     * Ignored tables
     *
     * KEY: table name
     * VALUE: whether to ignore
     */
    private final Map<String, Boolean> ignoreTables = new HashMap<>();

    public TenantDatabaseInterceptor(TenantProperties properties) {
        // Different DBs have different case conventions, so add both
        properties.getIgnoreTables().forEach(table -> {
            addIgnoreTable(table, true);
        });
        // In OracleKeyGenerator, when generating primary keys, this table is queried; after that TENANT_ID is automatically appended, which causes errors.
        addIgnoreTable("DUAL", true);
    }

    @Override
    public Expression getTenantId() {
        return new LongValue(TenantContextHolder.getRequiredTenantId());
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // Case 1: globally ignore multi-tenancy
        if (TenantContextHolder.isIgnore()) {
            return true;
        }
        // Case 2: tables that ignore multi-tenancy
        tableName = SqlParserUtils.removeWrapperSymbol(tableName);
        Boolean ignore = ignoreTables.get(tableName.toLowerCase());
        if (ignore == null) {
            ignore = computeIgnoreTable(tableName);
            synchronized (ignoreTables) {
                addIgnoreTable(tableName, ignore);
            }
        }
        return ignore;
    }

    private void addIgnoreTable(String tableName, boolean ignore) {
        ignoreTables.put(tableName.toLowerCase(), ignore);
        ignoreTables.put(tableName.toUpperCase(), ignore);
    }

    private boolean computeIgnoreTable(String tableName) {
        // Tables not found are not part of this project; do not intercept (ignore tenant)
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        if (tableInfo == null) {
            return true;
        }
        // If it extends the TenantBaseEntity base class, obviously do not ignore tenant
        if (TenantBaseEntity.class.isAssignableFrom(tableInfo.getEntityType())) {
            return false;
        }
        // If the @TenantIgnore annotation is added, ignore tenant
        TenantIgnore tenantIgnore = tableInfo.getEntityType().getAnnotation(TenantIgnore.class);
        return tenantIgnore != null;
    }

}
