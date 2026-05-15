package com.focela.platform.mybatis.core.query;

import com.focela.platform.mybatis.core.utils.JdbcUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * Extends MyBatis Plus QueryWrapper with additional features:
 *
 * 1. Adds xxxIfPresent methods that skip the condition when the value is absent.
 *
 * @param <T> entity type
 */
public class QueryWrapperX<T> extends QueryWrapper<T> {

    public QueryWrapperX<T> likeIfPresent(String column, String val) {
        if (StringUtils.hasText(val)) {
            return (QueryWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> inIfPresent(String column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> inIfPresent(String column, Object... values) {
        if (!ArrayUtils.isEmpty(values)) {
            return (QueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> eqIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> neIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.ne(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> gtIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> geIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> ltIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> leIfPresent(String column, Object val) {
        if (val != null) {
            return (QueryWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (QueryWrapperX<T>) super.between(column, val1, val2);
        }
        if (val1 != null) {
            return (QueryWrapperX<T>) ge(column, val1);
        }
        if (val2 != null) {
            return (QueryWrapperX<T>) le(column, val2);
        }
        return this;
    }

    public QueryWrapperX<T> betweenIfPresent(String column, Object[] values) {
        if (values!= null && values.length != 0 && values[0] != null && values[1] != null) {
            return (QueryWrapperX<T>) super.between(column, values[0], values[1]);
        }
        if (values!= null && values.length != 0 && values[0] != null) {
            return (QueryWrapperX<T>) ge(column, values[0]);
        }
        if (values!= null && values.length != 0 && values[1] != null) {
            return (QueryWrapperX<T>) le(column, values[1]);
        }
        return this;
    }

    // ========== Override parent methods for fluent chaining ==========

    @Override
    public QueryWrapperX<T> eq(boolean condition, String column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    @Override
    public QueryWrapperX<T> eq(String column, Object val) {
        super.eq(column, val);
        return this;
    }

    @Override
    public QueryWrapperX<T> orderByDesc(String column) {
        super.orderByDesc(true, column);
        return this;
    }

    @Override
    public QueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    @Override
    public QueryWrapperX<T> in(String column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }

    /**
     * Set to return only the last N records.
     *
     * TODO: not a perfect solution; needs more thought. With multiple data sources of different types, the syntax for limiting rows differs.
     *
     * @return this
     */
    public QueryWrapperX<T> limitN(int n) {
        DbType dbType = JdbcUtils.getDbType();
        switch (dbType) {
            case ORACLE:
            case ORACLE_12C:
                super.le("ROWNUM", n);
                break;
            case SQL_SERVER:
            case SQL_SERVER2005:
                super.select("TOP " + n + " *"); // SQL Server uses SELECT TOP n to limit rows, so we must use * to select remaining fields
                break;
            default: // MySQL, PostgreSQL, DM (Dameng), KingbaseES all use LIMIT
                super.last("LIMIT " + n);
        }
        return this;
    }

}
