package com.focela.platform.infra.repository.mapper.file;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.infra.controller.admin.file.request.FilePageRequest;
import com.focela.platform.infra.domain.entity.file.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * File operation Mapper
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
