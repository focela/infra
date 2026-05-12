package com.focela.platform.module.infra.repository.mapper.file;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.file.dto.config.FileConfigPageRequest;
import com.focela.platform.module.infra.repository.entity.file.FileConfigEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileConfigMapper extends BaseMapperX<FileConfigEntity> {

    default PageResult<FileConfigEntity> selectPage(FileConfigPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileConfigEntity>()
                .likeIfPresent(FileConfigEntity::getName, reqVO.getName())
                .eqIfPresent(FileConfigEntity::getStorage, reqVO.getStorage())
                .betweenIfPresent(FileConfigEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileConfigEntity::getId));
    }

    default FileConfigEntity selectByMaster() {
        return selectOne(FileConfigEntity::getMaster, true);
    }

}
