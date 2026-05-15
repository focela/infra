package com.focela.platform.infra.repository.mapper.database;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.infra.entity.database.DataSourceConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Data source config Mapper
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigEntity> {
}
