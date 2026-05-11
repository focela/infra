package com.focela.platform.module.infra.repository.mapper.codegen;

import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.repository.entity.codegen.CodegenColumnEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CodegenColumnMapper extends BaseMapperX<CodegenColumnEntity> {

    default List<CodegenColumnEntity> selectListByTableId(Long tableId) {
        return selectList(new LambdaQueryWrapperX<CodegenColumnEntity>()
                .eq(CodegenColumnEntity::getTableId, tableId)
                .orderByAsc(CodegenColumnEntity::getOrdinalPosition));
    }

    default void deleteListByTableId(Long tableId) {
        delete(CodegenColumnEntity::getTableId, tableId);
    }

    default void deleteListByTableId(Collection<Long> tableIds) {
        delete(new LambdaQueryWrapperX<CodegenColumnEntity>()
               .in(CodegenColumnEntity::getTableId, tableIds));
    }

}
