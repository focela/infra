package com.focela.platform.module.infra.repository.mapper.codegen;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.codegen.vo.table.CodegenTablePageReqVO;
import com.focela.platform.module.infra.repository.entity.codegen.CodegenTableEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CodegenTableMapper extends BaseMapperX<CodegenTableEntity> {

    default CodegenTableEntity selectByTableNameAndDataSourceConfigId(String tableName, Long dataSourceConfigId) {
        return selectOne(CodegenTableEntity::getTableName, tableName,
                CodegenTableEntity::getDataSourceConfigId, dataSourceConfigId);
    }

    default PageResult<CodegenTableEntity> selectPage(CodegenTablePageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CodegenTableEntity>()
                .likeIfPresent(CodegenTableEntity::getTableName, pageReqVO.getTableName())
                .likeIfPresent(CodegenTableEntity::getTableComment, pageReqVO.getTableComment())
                .likeIfPresent(CodegenTableEntity::getClassName, pageReqVO.getClassName())
                .betweenIfPresent(CodegenTableEntity::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(CodegenTableEntity::getUpdateTime));
    }

    default List<CodegenTableEntity> selectListByDataSourceConfigId(Long dataSourceConfigId) {
        return selectList(CodegenTableEntity::getDataSourceConfigId, dataSourceConfigId);
    }

    default List<CodegenTableEntity> selectListByTemplateTypeAndMasterTableId(Integer templateType, Long masterTableId) {
        return selectList(CodegenTableEntity::getTemplateType, templateType,
                CodegenTableEntity::getMasterTableId, masterTableId);
    }

}
