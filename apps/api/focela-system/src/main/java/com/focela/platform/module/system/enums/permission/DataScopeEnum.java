package com.focela.platform.module.system.enums.permission;

import com.focela.platform.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Data scope enum
 *
 * Used to implement data-level permissions.
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum implements ArrayValuable<Integer> {

    ALL(1), // all data permission

    DEPT_CUSTOM(2), // permission for specified departments
    DEPT_ONLY(3), // permission for the department only
    DEPT_AND_CHILD(4), // permission for the department and its children

    SELF(5); // permission for own data only

    /**
     * Scope
     */
    private final Integer scope;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(DataScopeEnum::getScope).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
