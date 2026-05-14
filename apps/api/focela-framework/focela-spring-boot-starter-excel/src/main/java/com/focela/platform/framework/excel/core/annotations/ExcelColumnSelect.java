package com.focela.platform.framework.excel.core.annotations;

import java.lang.annotation.*;

/**
 * Adds dropdown selection data to an Excel column.
 *
 * Specify either {@link #dictType()} or {@link #functionName()}; choose one.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelColumnSelect {

    /**
     * @return dictionary type
     */
    String dictType() default "";

    /**
     * @return name of the method that provides the dropdown data source
     */
    String functionName() default "";

}
