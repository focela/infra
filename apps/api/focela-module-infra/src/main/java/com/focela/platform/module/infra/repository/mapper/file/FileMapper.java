package com.focela.platform.module.infra.repository.mapper.file;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.file.dto.file.FilePageRequest;
import com.focela.platform.module.infra.repository.entity.file.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件操作 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FileMapper extends BaseMapperX<FileEntity> {

    default PageResult<FileEntity> selectPage(FilePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<FileEntity>()
                .likeIfPresent(FileEntity::getPath, request.getPath())
                .likeIfPresent(FileEntity::getType, request.getType())
                .betweenIfPresent(FileEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(FileEntity::getId));
    }

}
