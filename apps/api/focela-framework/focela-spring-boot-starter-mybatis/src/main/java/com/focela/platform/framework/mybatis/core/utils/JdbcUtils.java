package com.focela.platform.framework.mybatis.core.utils;

import com.focela.platform.framework.common.utils.object.ObjectUtils;
import com.focela.platform.framework.common.utils.spring.SpringUtils;
import com.focela.platform.framework.mybatis.core.enums.DbTypeEnum;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC utility class
 */
public class JdbcUtils {

    /**
     * Check whether the connection is valid.
     *
     * @param url      data source URL
     * @param username username
     * @param password password
     * @return whether valid
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Get the DB type corresponding to the URL.
     *
     * @param url URL
     * @return DB type
     */
    public static DbType getDbType(String url) {
        return com.baomidou.mybatisplus.extension.toolkit.JdbcUtils.getDbType(url);
    }

    /**
     * Get the DB type using the current database connection.
     *
     * @return DB type
     */
    public static DbType getDbType() {
        DataSource dataSource;
        try {
            DynamicRoutingDataSource dynamicRoutingDataSource = SpringUtils.getBean(DynamicRoutingDataSource.class);
            dataSource = dynamicRoutingDataSource.determineDataSource();
        } catch (NoSuchBeanDefinitionException e) {
            dataSource = SpringUtils.getBean(DataSource.class);
        }
        try (Connection conn = dataSource.getConnection()) {
            return DbTypeEnum.find(conn.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Check whether the JDBC connection is SQL Server.
     *
     * @param url JDBC URL
     * @return whether SQL Server
     */
    public static boolean isSQLServer(String url) {
        DbType dbType = getDbType(url);
        return isSQLServer(dbType);
    }

    /**
     * Check whether the JDBC connection is SQL Server.
     *
     * @param dbType DB type
     * @return whether SQL Server
     */
    public static boolean isSQLServer(DbType dbType) {
        return ObjectUtils.equalsAny(dbType, DbType.SQL_SERVER, DbType.SQL_SERVER2005);
    }

}
