package com.focela.platform.infra.service.file;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.file.dto.config.FileConfigPageRequest;
import com.focela.platform.infra.controller.admin.file.dto.config.FileConfigSaveRequest;
import com.focela.platform.infra.entity.file.FileConfigEntity;
import com.focela.platform.infra.config.file.client.FileClient;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 文件配置 Service 接口
 */
public interface FileConfigService {

    /**
     * 创建文件配置
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createFileConfig(@Valid FileConfigSaveRequest createRequest);

    /**
     * 更新文件配置
     *
     * @param updateRequest 更新信息
     */
    void updateFileConfig(@Valid FileConfigSaveRequest updateRequest);

    /**
     * 更新文件配置为 Master
     *
     * @param id 编号
     */
    void updateFileConfigMaster(Long id);

    /**
     * 删除文件配置
     *
     * @param id 编号
     */
    void deleteFileConfig(Long id);

    /**
     * 批量删除文件配置
     *
     * @param ids 编号列表
     */
    void deleteFileConfigList(List<Long> ids);

    /**
     * 获得文件配置
     *
     * @param id 编号
     * @return 文件配置
     */
    FileConfigEntity getFileConfig(Long id);

    /**
     * 获得文件配置分页
     *
     * @param pageRequest 分页查询
     * @return 文件配置分页
     */
    PageResult<FileConfigEntity> getFileConfigPage(FileConfigPageRequest pageRequest);

    /**
     * 测试文件配置是否正确，通过上传文件
     *
     * @param id 编号
     * @return 文件 URL
     */
    String testFileConfig(Long id) throws Exception;

    /**
     * 获得指定编号的文件客户端
     *
     * @param id 配置编号
     * @return 文件客户端
     */
    FileClient getFileClient(Long id);

    /**
     * 获得 Master 文件客户端
     *
     * @return 文件客户端
     */
    FileClient getMasterFileClient();

}
