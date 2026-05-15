package com.focela.platform.infra.converter.file;

import com.focela.platform.infra.controller.admin.file.dto.config.FileConfigSaveRequest;
import com.focela.platform.infra.entity.file.FileConfigEntity;
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
