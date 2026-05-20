package com.focela.platform.infra.service.database;

import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.mybatis.core.utils.JdbcUtils;
import com.focela.platform.infra.controller.admin.database.request.DataSourceConfigSaveRequest;
import com.focela.platform.infra.domain.entity.database.DataSourceConfigEntity;
import com.focela.platform.infra.repository.mapper.database.DataSourceConfigMapper;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_EXISTS;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.DATA_SOURCE_CONFIG_INVALID;

/**
 * Implementation class of the datasource config Service
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultDataSourceConfigService implements DataSourceConfigService {

    private final DataSourceConfigMapper dataSourceConfigMapper;

    private final DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public Long createDataSourceConfig(DataSourceConfigSaveRequest createRequest) {
        DataSourceConfigEntity config = BeanUtils.toBean(createRequest, DataSourceConfigEntity.class);
        validateConnectionOK(config);

        // Insert
        dataSourceConfigMapper.insert(config);
        // Return
        return config.getId();
    }

    @Override
    public void updateDataSourceConfig(DataSourceConfigSaveRequest updateRequest) {
        // Verify it exists
        validateDataSourceConfigExists(updateRequest.getId());
        DataSourceConfigEntity updateObj = BeanUtils.toBean(updateRequest, DataSourceConfigEntity.class);
        validateConnectionOK(updateObj);

        // Update
        dataSourceConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteDataSourceConfig(Long id) {
        // Verify it exists
        validateDataSourceConfigExists(id);
        // Delete
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
        // If id is 0, default to the master datasource
        if (Objects.equals(id, DataSourceConfigEntity.ID_MASTER)) {
            return buildMasterDataSourceConfig();
        }
        // Read from DB
        return dataSourceConfigMapper.selectById(id);
    }

    @Override
    public List<DataSourceConfigEntity> getDataSourceConfigList() {
        List<DataSourceConfigEntity> dataSourceConfigs = dataSourceConfigMapper.selectList();
        // Prepend the master datasource
        dataSourceConfigs.add(0, buildMasterDataSourceConfig());
        return dataSourceConfigs;
    }

    private void validateConnectionOK(DataSourceConfigEntity config) {
        boolean success = JdbcUtils.isConnectionOK(config.getUrl(), config.getUsername(), config.getPassword());
        if (!success) {
            throw exception(DATA_SOURCE_CONFIG_INVALID);
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
