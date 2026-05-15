package com.focela.platform.infra.converter.config;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.infra.controller.admin.config.dto.ConfigResponse;
import com.focela.platform.infra.controller.admin.config.dto.ConfigSaveRequest;
import com.focela.platform.infra.entity.config.ConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConfigConverter {

    ConfigConverter INSTANCE = Mappers.getMapper(ConfigConverter.class);

    PageResult<ConfigResponse> convertPage(PageResult<ConfigEntity> page);

    List<ConfigResponse> convertList(List<ConfigEntity> list);

    @Mapping(source = "configKey", target = "key")
    ConfigResponse convert(ConfigEntity bean);

    @Mapping(source = "key", target = "configKey")
    ConfigEntity convert(ConfigSaveRequest bean);

}
