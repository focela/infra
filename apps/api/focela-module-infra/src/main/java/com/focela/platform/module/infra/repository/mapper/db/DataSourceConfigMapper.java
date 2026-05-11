package com.focela.platform.module.infra.repository.mapper.db;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.module.infra.repository.entity.db.DataSourceConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigEntity> {
}
