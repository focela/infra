package com.focela.platform.excel.core.converter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Money converter.
 *
 * Money unit: cents.
 */
public class MoneyConverter implements Converter<Integer> {

    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("not supported yet, and not needed");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("not supported yet, and not needed");
    }

    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        BigDecimal result = BigDecimal.valueOf(value)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        return new WriteCellData<>(result.toString());
    }

}
