package com.focela.platform.module.infra.repository.mapper.file;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import com.focela.platform.module.infra.repository.entity.file.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件操作 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FileMapper extends BaseMapperX<FileEntity> {

    default PageResult<FileEntity> selectPage(FilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileEntity>()
                .likeIfPresent(FileEntity::getPath, reqVO.getPath())
                .likeIfPresent(FileEntity::getType, reqVO.getType())
                .betweenIfPresent(FileEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileEntity::getId));
    }

}
