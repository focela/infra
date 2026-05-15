package com.focela.platform.system.enums.social;

import cn.hutool.core.util.ArrayUtil;
import com.focela.platform.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Supported OAuth2 social login providers.
 */
@Getter
@AllArgsConstructor
public enum SocialTypeEnum implements ArrayValuable<Integer> {

    GITEE(10, "GITEE"),
    DINGTALK(20, "DINGTALK"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(SocialTypeEnum::getType).toArray(Integer[]::new);

    /**
     * Type
     */
    private final Integer type;
    /**
     * Type identifier
     */
    private final String source;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static SocialTypeEnum valueOfType(Integer type) {
        return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
    }

}
