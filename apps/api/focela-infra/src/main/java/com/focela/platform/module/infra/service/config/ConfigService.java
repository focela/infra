package com.focela.platform.module.infra.service.config;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.infra.controller.admin.config.dto.ConfigPageRequest;
import com.focela.platform.module.infra.controller.admin.config.dto.ConfigSaveRequest;
import com.focela.platform.module.infra.entity.config.ConfigEntity;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 参数配置 Service 接口
 */
public interface ConfigService {

    /**
     * 创建参数配置
     *
     * @param createRequest 创建信息
     * @return 配置编号
     */
    Long createConfig(@Valid ConfigSaveRequest createRequest);

    /**
     * 更新参数配置
     *
     * @param updateRequest 更新信息
     */
    void updateConfig(@Valid ConfigSaveRequest updateRequest);

    /**
     * 删除参数配置
     *
     * @param id 配置编号
     */
    void deleteConfig(Long id);

    /**
     * 批量删除参数配置
     *
     * @param ids 配置编号列表
     */
    void deleteConfigList(List<Long> ids);

    /**
     * 获得参数配置
     *
     * @param id 配置编号
     * @return 参数配置
     */
    ConfigEntity getConfig(Long id);

    /**
     * 根据参数键，获得参数配置
     *
     * @param key 配置键
     * @return 参数配置
     */
    ConfigEntity getConfigByKey(String key);

    /**
     * 获得参数配置分页列表
     *
     * @param request 分页条件
     * @return 分页列表
     */
    PageResult<ConfigEntity> getConfigPage(ConfigPageRequest request);

}
