package com.focela.platform.framework.common.enums;

import cn.hutool.core.util.ArrayUtil;
import com.focela.platform.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Date interval enum.
 */
@Getter
@AllArgsConstructor
public enum DateIntervalEnum implements ArrayValuable<Integer> {

    HOUR(0, "Hour"), // Special: not present in dictionary; rarely needed in practice.
    DAY(1, "Day"),
    WEEK(2, "Week"),
    MONTH(3, "Month"),
    QUARTER(4, "Quarter"),
    YEAR(5, "Year")
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(DateIntervalEnum::getInterval).toArray(Integer[]::new);

    /**
     * Type.
     */
    private final Integer interval;
    /**
     * Name.
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static DateIntervalEnum valueOf(Integer interval) {
        return ArrayUtil.firstMatch(item -> item.getInterval().equals(interval), DateIntervalEnum.values());
    }

}
