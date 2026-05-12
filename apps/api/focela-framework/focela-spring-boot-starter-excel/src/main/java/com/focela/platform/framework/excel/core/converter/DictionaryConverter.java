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
 * Excel 数据字典转换器
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
        // 使用字典解析
        String type = getType(contentProperty);
        String label = readCellData.getStringValue();
        String value = DictionaryFrameworkUtils.parseDictDataValue(type, label);
        if (value == null) {
            log.error("[convertToJavaData][type({}) cannot parse label({})]", type, label);
            return null;
        }
        // 将 String 的 value 转换成对应的属性
        Class<?> fieldClazz = contentProperty.getField().getType();
        return Convert.convert(fieldClazz, value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        // 空时，返回空
        if (object == null) {
            return new WriteCellData<>("");
        }

        // 使用字典格式化
        String type = getType(contentProperty);
        String value = String.valueOf(object);
        String label = DictionaryFrameworkUtils.parseDictDataLabel(type, value);
        if (label == null) {
            log.error("[convertToExcelData][type({}) 转换not label({})]", type, value);
            return new WriteCellData<>("");
        }
        // 生成 Excel 小表格
        return new WriteCellData<>(label);
    }

    private static String getType(ExcelContentProperty contentProperty) {
        return contentProperty.getField().getAnnotation(DictionaryFormat.class).value();
    }

}
