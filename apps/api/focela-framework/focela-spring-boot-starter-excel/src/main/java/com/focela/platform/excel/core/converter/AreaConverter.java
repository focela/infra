package com.focela.platform.excel.core.converter;

import cn.hutool.core.convert.Convert;
import com.focela.platform.ip.core.Area;
import com.focela.platform.ip.core.utils.AreaUtils;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel area data converter.
 */
@Slf4j
public class AreaConverter implements Converter<Object> {

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
        // Parse the area ID
        String label = readCellData.getStringValue();
        Area area = AreaUtils.parseArea(label);
        if (area == null) {
            log.error("[convertToJavaData][label({}) cannot parse]", label);
            return null;
        }
        // Convert the value to the matching field type
        Class<?> fieldClazz = contentProperty.getField().getType();
        return Convert.convert(fieldClazz, area.getId());
    }

}
