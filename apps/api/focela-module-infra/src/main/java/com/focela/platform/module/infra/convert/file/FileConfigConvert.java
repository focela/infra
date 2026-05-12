package com.focela.platform.module.infra.convert.file;

import com.focela.platform.module.infra.controller.admin.file.dto.config.FileConfigSaveRequest;
import com.focela.platform.module.infra.repository.entity.file.FileConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 文件配置 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface FileConfigConvert {

    FileConfigConvert INSTANCE = Mappers.getMapper(FileConfigConvert.class);

    @Mapping(target = "config", ignore = true)
    FileConfigEntity convert(FileConfigSaveRequest bean);

}
