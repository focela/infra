package com.focela.platform.module.infra.enums.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigTypeEnum {

    /**
     * System config
     */
    SYSTEM(1),
    /**
     * Custom config
     */
    CUSTOM(2);

    private final Integer type;

}
