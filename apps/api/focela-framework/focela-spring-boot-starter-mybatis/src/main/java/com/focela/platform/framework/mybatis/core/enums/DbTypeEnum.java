package com.focela.platform.framework.mybatis.core.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enhancement of MyBatis Plus {@link DbType} with additional information.
 */
@Getter
@AllArgsConstructor
public enum DbTypeEnum {

    /**
     * H2
     *
     * Note: H2 does not support the find_in_set function.
     */
    H2(DbType.H2, "H2", ""),

    /**
     * MySQL
     */
    MY_SQL(DbType.MYSQL, "MySQL", "FIND_IN_SET('#{value}', #{column}) <> 0"),

    /**
     * Oracle
     */
    ORACLE(DbType.ORACLE, "Oracle", "FIND_IN_SET('#{value}', #{column}) <> 0"),

    /**
     * PostgreSQL
     *
     * Huawei openGauss uses the same ProductName as PostgreSQL.
     */
    POSTGRE_SQL(DbType.POSTGRE_SQL,"PostgreSQL", "POSITION('#{value}' IN #{column}) <> 0"),

    /**
     * SQL Server
     */
    SQL_SERVER(DbType.SQL_SERVER, "Microsoft SQL Server", "CHARINDEX(',' + #{value} + ',', ',' + #{column} + ',') <> 0"),
    /**
     * SQL Server 2005
     */
    SQL_SERVER2005(DbType.SQL_SERVER2005, "Microsoft SQL Server 2005", "CHARINDEX(',' + #{value} + ',', ',' + #{column} + ',') <> 0"),

    /**
     * DM (Dameng)
     */
    DM(DbType.DM, "DM DBMS", "FIND_IN_SET('#{value}', #{column}) <> 0"),

    /**
     * Kingbase
     */
    KINGBASE_ES(DbType.KINGBASE_ES, "KingbaseES", "POSITION('#{value}' IN #{column}) <> 0"),

    /**
     * OceanBase
     */
    OCEAN_BASE(DbType.OCEAN_BASE, "OceanBase", "FIND_IN_SET('#{value}', #{column}) <> 0")

    ;

    public static final Map<String, DbTypeEnum> MAP_BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(DbTypeEnum::getProductName, Function.identity()));

    public static final Map<DbType, DbTypeEnum> MAP_BY_MP = Arrays.stream(values())
            .collect(Collectors.toMap(DbTypeEnum::getMpDbType, Function.identity()));

    /**
     * MyBatis Plus DbType
     */
    private final DbType mpDbType;
    /**
     * Database product name
     */
    private final String productName;
    /**
     * SQL FIND_IN_SET template
     */
    private final String findInSetTemplate;

    public static DbType find(String databaseProductName) {
        if (StrUtil.isBlank(databaseProductName)) {
            return null;
        }
        return MAP_BY_NAME.get(databaseProductName).getMpDbType();
    }

    public static String getFindInSetTemplate(DbType dbType) {
        return Optional.of(MAP_BY_MP.get(dbType).getFindInSetTemplate())
                .orElseThrow(() -> new IllegalArgumentException("FIND_IN_SET not supported"));
    }
}
