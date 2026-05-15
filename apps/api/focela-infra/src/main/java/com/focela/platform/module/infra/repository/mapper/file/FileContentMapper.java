package com.focela.platform.module.infra.repository.mapper.file;

import com.focela.platform.module.infra.entity.file.FileContentEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileContentMapper extends BaseMapper<FileContentEntity> {

    default void deleteByConfigIdAndPath(Long configId, String path) {
        this.delete(new LambdaQueryWrapper<FileContentEntity>()
                .eq(FileContentEntity::getConfigId, configId)
                .eq(FileContentEntity::getPath, path));
    }

    default List<FileContentEntity> selectListByConfigIdAndPath(Long configId, String path) {
        return selectList(new LambdaQueryWrapper<FileContentEntity>()
                .eq(FileContentEntity::getConfigId, configId)
                .eq(FileContentEntity::getPath, path));
    }

}
