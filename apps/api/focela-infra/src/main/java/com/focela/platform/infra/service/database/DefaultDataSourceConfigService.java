package com.focela.platform.infra.service.database;

import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.mybatis.core.utils.JdbcUtils;
import com.focela.platform.infra.controller.admin.database.dto.DataSourceConfigSaveRequest;
import com.focela.platform.infra.entity.database.DataSourceConfigEntity;
import com.focela.platform.infra.repository.mapper.database.DataSourceConfigMapper;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.infra.constants.ErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_EXISTS;
import static com.focela.platform.infra.constants.ErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_OK;

/**
 * 数据源配置 Service 实现类
 */
@Service
@Validated
public class DefaultDataSourceConfigService implements DataSourceConfigService {

    @Resource
    private DataSourceConfigMapper dataSourceConfigMapper;

    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public Long createDataSourceConfig(DataSourceConfigSaveRequest createRequest) {
        DataSourceConfigEntity config = BeanUtils.toBean(createRequest, DataSourceConfigEntity.class);
        validateConnectionOK(config);

        // 插入
        dataSourceConfigMapper.insert(config);
        // 返回
        return config.getId();
    }

    @Override
    public void updateDataSourceConfig(DataSourceConfigSaveRequest updateRequest) {
        // 校验存在
        validateDataSourceConfigExists(updateRequest.getId());
        DataSourceConfigEntity updateObj = BeanUtils.toBean(updateRequest, DataSourceConfigEntity.class);
        validateConnectionOK(updateObj);

        // 更新
        dataSourceConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteDataSourceConfig(Long id) {
        // 校验存在
        validateDataSourceConfigExists(id);
        // 删除
        dataSourceConfigMapper.deleteById(id);
    }

    @Override
    public void deleteDataSourceConfigList(List<Long> ids) {
        dataSourceConfigMapper.deleteByIds(ids);
    }

    private void validateDataSourceConfigExists(Long id) {
        if (dataSourceConfigMapper.selectById(id) == null) {
            throw exception(DATA_SOURCE_CONFIG_NOT_EXISTS);
        }
    }

    @Override
    public DataSourceConfigEntity getDataSourceConfig(Long id) {
        // 如果 id 为 0，默认为 master 的数据源
        if (Objects.equals(id, DataSourceConfigEntity.ID_MASTER)) {
            return buildMasterDataSourceConfig();
        }
        // 从 DB 中读取
        return dataSourceConfigMapper.selectById(id);
    }

    @Override
    public List<DataSourceConfigEntity> getDataSourceConfigList() {
        List<DataSourceConfigEntity> result = dataSourceConfigMapper.selectList();
        // 补充 master 数据源
        result.add(0, buildMasterDataSourceConfig());
        return result;
    }

    private void validateConnectionOK(DataSourceConfigEntity config) {
        boolean success = JdbcUtils.isConnectionOK(config.getUrl(), config.getUsername(), config.getPassword());
        if (!success) {
            throw exception(DATA_SOURCE_CONFIG_NOT_OK);
        }
    }

    private DataSourceConfigEntity buildMasterDataSourceConfig() {
        String primary = dynamicDataSourceProperties.getPrimary();
        DataSourceProperty dataSourceProperty = dynamicDataSourceProperties.getDatasource().get(primary);
        return new DataSourceConfigEntity().setId(DataSourceConfigEntity.ID_MASTER).setName(primary)
                .setUrl(dataSourceProperty.getUrl())
                .setUsername(dataSourceProperty.getUsername())
                .setPassword(dataSourceProperty.getPassword());
    }

}
