package com.focela.platform.infra.repository.mapper.database;

import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.infra.domain.entity.database.DataSourceConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Data source config Mapper
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigEntity> {
}
