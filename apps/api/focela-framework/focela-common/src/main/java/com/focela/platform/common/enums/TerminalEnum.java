package com.focela.platform.common.enums;

import com.focela.platform.common.core.ArrayValuable;
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
     * Terminal.
     */
    private final Integer terminal;
    /**
     * Terminal name.
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
