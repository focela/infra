package com.focela.platform.excel.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
