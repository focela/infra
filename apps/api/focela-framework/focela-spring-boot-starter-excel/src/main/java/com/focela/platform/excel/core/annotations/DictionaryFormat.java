package com.focela.platform.excel.core.annotations;

import java.lang.annotation.*;

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
     * For example, SysDictTypeConstants or InfDictTypeConstants.
     *
     * @return dictionary type
     */
    String value();

}
