package com.focela.platform.infra.service.database;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.mybatis.core.utils.JdbcUtils;
import com.focela.platform.infra.entity.database.DataSourceConfigEntity;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation class of the database table Service
 */
@Service
@RequiredArgsConstructor
public class DefaultDatabaseTableService implements DatabaseTableService {

    private final DataSourceConfigService dataSourceConfigService;

    @Override
    public List<TableInfo> getTableList(Long dataSourceConfigId, String nameLike, String commentLike) {
        List<TableInfo> tables = getTableList0(dataSourceConfigId, null);
        return tables.stream().filter(tableInfo -> (StrUtil.isEmpty(nameLike) || tableInfo.getName().contains(nameLike))
                        && (StrUtil.isEmpty(commentLike) || tableInfo.getComment().contains(commentLike)))
                .collect(Collectors.toList());
    }

    @Override
    public TableInfo getTable(Long dataSourceConfigId, String name) {
        return CollUtil.getFirst(getTableList0(dataSourceConfigId, name));
    }

    private List<TableInfo> getTableList0(Long dataSourceConfigId, String name) {
        // Get the datasource config
        DataSourceConfigEntity config = dataSourceConfigService.getDataSourceConfig(dataSourceConfigId);
        Assert.notNull(config, "datasource ({}) does not exist!", dataSourceConfigId);

        // Use MyBatis Plus Generator to parse the table structure
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(config.getUrl(), config.getUsername(),
                config.getPassword());
        if (JdbcUtils.isSQLServer(config.getUrl())) { // Special case: SQLServer JDBC is non-standard, see https://github.com/baomidou/mybatis-plus/issues/5419
            dataSourceConfigBuilder.databaseQueryClass(SQLQuery.class);
        }
        StrategyConfig.Builder strategyConfig = new StrategyConfig.Builder().enableSkipView(); // Skip views, rarely used in business
        if (StrUtil.isNotEmpty(name)) {
            strategyConfig.addInclude(name);
        } else {
            // Exclude workflow and scheduled-job prefixed tables
            strategyConfig.addExclude("ACT_[\\S\\s]+|QRTZ_[\\S\\s]+|FLW_[\\S\\s]+|act_[\\S\\s]+|qrtz_[\\S\\s]+|flw_[\\S\\s]+");
            // Exclude ORACLE system tables
            strategyConfig.addExclude("IMPDP_[\\S\\s]+|ALL_[\\S\\s]+|HS_[\\S\\s]+|impdp_[\\S\\s]+|all_[\\S\\s]+|hs_[\\S\\s]+");
            strategyConfig.addExclude("[\\S\\s]+\\$[\\S\\s]+|[\\S\\s]+\\$"); // Tables containing $ are usually system tables
        }

        GlobalConfig globalConfig = new GlobalConfig.Builder().dateType(DateType.TIME_PACK).build(); // Use LocalDateTime only, not LocalDate
        ConfigBuilder builder = new ConfigBuilder(null, dataSourceConfigBuilder.build(), strategyConfig.build(),
                null, globalConfig, null);
        // Sort by name
        List<TableInfo> tables = builder.getTableInfoList();
        tables.sort(Comparator.comparing(TableInfo::getName));
        return tables;
    }

}
