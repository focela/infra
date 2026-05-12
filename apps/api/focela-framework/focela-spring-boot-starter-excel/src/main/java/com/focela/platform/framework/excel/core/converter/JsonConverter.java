package com.focela.platform.framework.excel.core.converter;

import com.focela.platform.framework.common.utils.json.JsonUtils;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;

/**
 * Excel Json 转换器
 */
public class JsonConverter implements Converter<Object> {

    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("not supported yet, and not needed");
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("not supported yet, and not needed");
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        // 生成 Excel 小表格
        return new WriteCellData<>(JsonUtils.toJsonString(value));
    }

}
