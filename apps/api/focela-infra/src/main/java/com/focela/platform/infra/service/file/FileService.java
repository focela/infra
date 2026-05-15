package com.focela.platform.infra.service.file;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.infra.controller.admin.file.dto.FileCreateRequest;
import com.focela.platform.infra.controller.admin.file.dto.FilePageRequest;
import com.focela.platform.infra.controller.admin.file.dto.FilePresignedUrlResponse;
import com.focela.platform.infra.entity.file.FileEntity;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 文件 Service 接口
 */
public interface FileService {

    /**
     * 获得文件分页
     *
     * @param pageRequest 分页查询
     * @return 文件分页
     */
    PageResult<FileEntity> getFilePage(FilePageRequest pageRequest);

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content   文件内容
     * @param name      文件名称，允许空
     * @param directory 目录，允许空
     * @param type      文件的 MIME 类型，允许空
     * @return 文件路径
     */
    String createFile(@NotEmpty(message = "file content must not be blank") byte[] content,
                      String name, String directory, String type);

    /**
     * 生成文件预签名地址信息，用于上传
     *
     * @param name      文件名
     * @param directory 目录
     * @return 预签名地址信息
     */
    FilePresignedUrlResponse presignPutUrl(@NotEmpty(message = "file name must not be blank") String name,
                                         String directory);
    /**
     * 生成文件预签名地址信息，用于读取
     *
     * @param url 完整的文件访问地址
     * @param expirationSeconds 访问有效期，单位秒
     * @return 文件预签名地址
     */
    String presignGetUrl(String url, Integer expirationSeconds);

    /**
     * 创建文件
     *
     * @param createRequest 创建信息
     * @return 编号
     */
    Long createFile(FileCreateRequest createRequest);
    FileEntity getFile(Long id);

    /**
     * 删除文件
     *
     * @param id 编号
     */
    void deleteFile(Long id) throws Exception;

    /**
     * 批量删除文件
     *
     * @param ids 编号列表
     */
    void deleteFileList(List<Long> ids) throws Exception;

    /**
     * 获得文件内容
     *
     * @param configId 配置编号
     * @param path     文件路径
     * @return 文件内容
     */
    byte[] getFileContent(Long configId, String path) throws Exception;

}
