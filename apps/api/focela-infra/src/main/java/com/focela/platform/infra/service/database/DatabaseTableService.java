package com.focela.platform.infra.service.database;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;

import java.util.List;

/**
 * Database table Service
 */
public interface DatabaseTableService {

    /**
     * Get the list of tables, fuzzy-matched by table name + table description.
     *
     * @param dataSourceConfigId datasource config ID
     * @param nameLike           table name, fuzzy match
     * @param commentLike        table description, fuzzy match
     * @return list of tables
     */
    List<TableInfo> getTableList(Long dataSourceConfigId, String nameLike, String commentLike);

    /**
     * Get the specified table by name.
     *
     * @param dataSourceConfigId datasource config ID
     * @param tableName          table name
     * @return table
     */
    TableInfo getTable(Long dataSourceConfigId, String tableName);

}
