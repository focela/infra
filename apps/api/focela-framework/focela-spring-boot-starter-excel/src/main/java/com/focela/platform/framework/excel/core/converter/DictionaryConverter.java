package com.focela.platform.framework.excel.core.converter;

import cn.hutool.core.convert.Convert;
import com.focela.platform.framework.dictionary.core.DictionaryFrameworkUtils;
import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel dictionary data converter.
 */
@Slf4j
public class DictionaryConverter implements Converter<Object> {

    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("not supported yet, and not needed");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("not supported yet, and not needed");
    }

    @Override
    public Object convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        // Resolve via dictionary
        String type = getType(contentProperty);
        String label = readCellData.getStringValue();
        String value = DictionaryFrameworkUtils.parseDictDataValue(type, label);
        if (value == null) {
            log.error("[convertToJavaData][type({}) cannot parse label({})]", type, label);
            return null;
        }
        // Convert the String value to the matching field type
        Class<?> fieldClazz = contentProperty.getField().getType();
        return Convert.convert(fieldClazz, value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        // If null, return empty
        if (object == null) {
            return new WriteCellData<>("");
        }

        // Format via dictionary
        String type = getType(contentProperty);
        String value = String.valueOf(object);
        String label = DictionaryFrameworkUtils.parseDictDataLabel(type, value);
        if (label == null) {
            log.error("[convertToExcelData][type({}) cannot convert label({})]", type, value);
            return new WriteCellData<>("");
        }
        // Build the Excel cell value
        return new WriteCellData<>(label);
    }

    private static String getType(ExcelContentProperty contentProperty) {
        return contentProperty.getField().getAnnotation(DictionaryFormat.class).value();
    }

}
