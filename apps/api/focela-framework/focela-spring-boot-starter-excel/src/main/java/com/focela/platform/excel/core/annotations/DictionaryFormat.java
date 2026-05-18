package com.focela.platform.excel.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Dictionary formatter.
 *
 * Formats a dictionary data value into its corresponding label.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DictionaryFormat {

    /**
     * For example, the values defined in {@code DictionaryTypeConstants}.
     *
     * @return dictionary type
     */
    String value();

}
