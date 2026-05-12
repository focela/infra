package com.focela.platform.framework.common.enums;

import com.focela.platform.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Client terminal types.
 */
@RequiredArgsConstructor
@Getter
public enum TerminalEnum implements ArrayValuable<Integer> {

    UNKNOWN(0, "Unknown"),
    H5(20, "H5 Web"),
    APP(31, "Mobile App"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(TerminalEnum::getTerminal).toArray(Integer[]::new);

    /**
     * 终端
     */
    private final Integer terminal;
    /**
     * 终端名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
