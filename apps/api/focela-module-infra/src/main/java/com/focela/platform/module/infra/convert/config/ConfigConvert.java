package com.focela.platform.module.infra.convert.config;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.focela.platform.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.focela.platform.module.infra.repository.entity.config.ConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConfigConvert {

    ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);

    PageResult<ConfigRespVO> convertPage(PageResult<ConfigEntity> page);

    List<ConfigRespVO> convertList(List<ConfigEntity> list);

    @Mapping(source = "configKey", target = "key")
    ConfigRespVO convert(ConfigEntity bean);

    @Mapping(source = "key", target = "configKey")
    ConfigEntity convert(ConfigSaveReqVO bean);

}
