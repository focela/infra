package com.focela.platform.module.infra.convert.config;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.module.infra.controller.admin.config.dto.ConfigResponse;
import com.focela.platform.module.infra.controller.admin.config.dto.ConfigSaveRequest;
import com.focela.platform.module.infra.repository.entity.config.ConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConfigConvert {

    ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);

    PageResult<ConfigResponse> convertPage(PageResult<ConfigEntity> page);

    List<ConfigResponse> convertList(List<ConfigEntity> list);

    @Mapping(source = "configKey", target = "key")
    ConfigResponse convert(ConfigEntity bean);

    @Mapping(source = "key", target = "configKey")
    ConfigEntity convert(ConfigSaveRequest bean);

}
