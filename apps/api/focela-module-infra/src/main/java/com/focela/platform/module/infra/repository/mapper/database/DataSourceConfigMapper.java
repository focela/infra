package com.focela.platform.module.infra.repository.mapper.database;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.module.infra.entity.database.DataSourceConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigEntity> {
}
