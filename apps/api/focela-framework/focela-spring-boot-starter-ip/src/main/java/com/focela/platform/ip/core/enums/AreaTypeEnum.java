package com.focela.platform.ip.core.enums;

import com.focela.platform.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Area type enum
 */
@AllArgsConstructor
@Getter
public enum AreaTypeEnum implements ArrayValuable<Integer> {

    COUNTRY(1, "Country"),
    PROVINCE(2, "Province"),
    CITY(3, "City"),
    DISTRICT(4, "District"), // counties, towns, districts, etc.
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AreaTypeEnum::getType).toArray(Integer[]::new);

    /**
     * Type
     */
    private final Integer type;
    /**
     * Name
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
