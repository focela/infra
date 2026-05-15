package com.focela.platform.infra.repository.mapper.file;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.infra.controller.admin.file.dto.config.FileConfigPageRequest;
import com.focela.platform.infra.entity.file.FileConfigEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileConfigMapper extends BaseMapperX<FileConfigEntity> {

    default PageResult<FileConfigEntity> selectPage(FileConfigPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<FileConfigEntity>()
                .likeIfPresent(FileConfigEntity::getName, request.getName())
                .eqIfPresent(FileConfigEntity::getStorage, request.getStorage())
                .betweenIfPresent(FileConfigEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(FileConfigEntity::getId));
    }

    default FileConfigEntity selectByMaster() {
        return selectOne(FileConfigEntity::getMaster, true);
    }

}
