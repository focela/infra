package com.focela.platform.mybatis.core.type;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.utils.string.StrUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * TypeHandler for Set&lt;Long&gt;, corresponding to the database varchar type.
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(List.class)
public class LongSetTypeHandler implements TypeHandler<Set<Long>> {

    private static final String COMMA = ",";

    @Override
    public void setParameter(PreparedStatement ps, int i, Set<Long> strings, JdbcType jdbcType) throws SQLException {
        // Set the placeholder
        ps.setString(i, CollUtil.join(strings, COMMA));
    }

    @Override
    public Set<Long> getResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return getResult(value);
    }

    @Override
    public Set<Long> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return getResult(value);
    }

    @Override
    public Set<Long> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return getResult(value);
    }

    private Set<Long> getResult(String value) {
        if (value == null) {
            return null;
        }
        return StrUtils.splitToLongSet(value, COMMA);
    }
}
