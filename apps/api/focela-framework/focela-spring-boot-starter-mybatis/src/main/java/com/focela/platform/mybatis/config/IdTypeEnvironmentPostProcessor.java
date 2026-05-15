package com.focela.platform.mybatis.config;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.utils.collection.SetUtils;
import com.focela.platform.mybatis.core.utils.JdbcUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * When IdType is {@link IdType#NONE}, automatically set it based on the database used by the PRIMARY data source.
 */
@Slf4j
public class IdTypeEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String ID_TYPE_KEY = "mybatis-plus.global-config.db-config.id-type";

    private static final String DATASOURCE_DYNAMIC_KEY = "spring.datasource.dynamic";

    private static final String QUARTZ_JOB_STORE_DRIVER_KEY = "spring.quartz.properties.org.quartz.jobStore.driverDelegateClass";

    private static final Set<DbType> INPUT_ID_TYPES = SetUtils.asSet(DbType.ORACLE, DbType.ORACLE_12C,
            DbType.POSTGRE_SQL, DbType.KINGBASE_ES, DbType.DB2, DbType.H2);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // If DbType cannot be obtained, skip processing
        DbType dbType = getDbType(environment);
        if (dbType == null) {
            return;
        }

        // Set the Quartz JobStore Driver
        // TODO: no particularly suitable place yet; keep here for now
        setJobStoreDriverIfPresent(environment, dbType);

        // If not NONE, skip processing
        IdType idType = getIdType(environment);
        if (idType != IdType.NONE) {
            return;
        }
        // Case 1: user-input ID, suitable for Oracle, PostgreSQL, Kingbase, DB2, H2
        if (INPUT_ID_TYPES.contains(dbType)) {
            setIdType(environment, IdType.INPUT);
            return;
        }
        // Case 2: auto-increment ID, suitable for databases like MySQL and DM with native auto-increment
        setIdType(environment, IdType.AUTO);
    }

    public IdType getIdType(ConfigurableEnvironment environment) {
        String value = environment.getProperty(ID_TYPE_KEY);
        try {
            return StrUtil.isNotBlank(value) ? IdType.valueOf(value) : IdType.NONE;
        } catch (IllegalArgumentException ex) {
            log.error("[getIdType][cannot parse id-type config value({})]", value, ex);
            return IdType.NONE;
        }
    }

    public void setIdType(ConfigurableEnvironment environment, IdType idType) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID_TYPE_KEY, idType);
        environment.getPropertySources().addFirst(new MapPropertySource("mybatisPlusIdType", map));
        log.info("[setIdType][update MyBatis Plus idType is ({})]", idType);
    }

    public void setJobStoreDriverIfPresent(ConfigurableEnvironment environment, DbType dbType) {
        String driverClass = environment.getProperty(QUARTZ_JOB_STORE_DRIVER_KEY);
        if (StrUtil.isNotEmpty(driverClass)) {
            return;
        }
        // Get the corresponding driverClass for the dbType
        switch (dbType) {
            case POSTGRE_SQL:
                driverClass = "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate";
                break;
            case ORACLE:
            case ORACLE_12C:
                driverClass = "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate";
                break;
            case SQL_SERVER:
            case SQL_SERVER2005:
                driverClass = "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
                break;
            case DM:
            case KINGBASE_ES:
                driverClass = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
                break;
        }
        // Set the driverClass property
        if (StrUtil.isNotEmpty(driverClass)) {
            environment.getSystemProperties().put(QUARTZ_JOB_STORE_DRIVER_KEY, driverClass);
        }
    }

    public static DbType getDbType(ConfigurableEnvironment environment) {
        String primary = environment.getProperty(DATASOURCE_DYNAMIC_KEY + "." + "primary");
        if (StrUtil.isEmpty(primary)) {
            return null;
        }
        String url = environment.getProperty(DATASOURCE_DYNAMIC_KEY + ".datasource." + primary + ".url");
        if (StrUtil.isEmpty(url)) {
            return null;
        }
        return JdbcUtils.getDbType(url);
    }

}
