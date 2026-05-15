package com.focela.platform.system.enums.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Menu type enum
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum {

    DIR(1), // directory
    MENU(2), // menu
    BUTTON(3) // button
    ;

    /**
     * Type
     */
    private final Integer type;

}
