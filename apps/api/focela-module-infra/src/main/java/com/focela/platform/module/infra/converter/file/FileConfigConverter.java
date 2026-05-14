package com.focela.platform.module.infra.converter.file;

import com.focela.platform.module.infra.controller.admin.file.dto.config.FileConfigSaveRequest;
import com.focela.platform.module.infra.entity.file.FileConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * File config Converter
 */
@Mapper
public interface FileConfigConverter {

    FileConfigConverter INSTANCE = Mappers.getMapper(FileConfigConverter.class);

    @Mapping(target = "config", ignore = true)
    FileConfigEntity convert(FileConfigSaveRequest bean);

}
