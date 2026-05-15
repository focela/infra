package com.focela.platform.excel.core.function;

import java.util.List;

/**
 * Interface for fetching Excel column dropdown data sources.
 *
 * Why not parse the dictionary directly? Some dropdown data is not sourced from
 * the dictionary, so this interface keeps things compatible across both cases.
 */
public interface ExcelColumnSelectFunction {

    /**
     * Get the method name.
     *
     * @return method name
     */
    String getName();

    /**
     * Get the column dropdown data source.
     *
     * @return dropdown data source
     */
    List<String> getOptions();

}
