package com.focela.platform.excel.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.focela.platform.common.core.KeyValue;
import com.focela.platform.dictionary.core.DictionaryFrameworkUtils;
import com.focela.platform.excel.core.annotations.ExcelColumnSelect;
import com.focela.platform.excel.core.function.ExcelColumnSelectFunction;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

/**
 * Implements dropdown lists backed by a fixed reference sheet.
 */
@Slf4j
public class SelectSheetWriteHandler implements SheetWriteHandler {

    /**
     * Data start row (zero-based).
     *
     * Convention: this project's first row is the header, so we start from 1. If
     * your Excel has multiple header rows, adjust accordingly.
     */
    public static final int FIRST_ROW = 1;
    /**
     * Number of rows that need a dropdown; defaults to 2000. Adjust if you need more.
     */
    public static final int LAST_ROW = 2000;

    private static final String DICT_SHEET_NAME = "dict sheet";

    /**
     * key: column index, value: dropdown data source.
     */
    private final Map<Integer, List<String>> selectMap = new HashMap<>();

    public SelectSheetWriteHandler(Class<?> head) {
        // Parse dropdown data
        int colIndex = 0;
        boolean ignoreUnannotated = head.isAnnotationPresent(ExcelIgnoreUnannotated.class);
        for (Field field : head.getDeclaredFields()) {
            // See https://github.com/YunaiV/ruoyi-vue-pro/pull/853
            // 1.1 Skip static final or transient fields
            if (isStaticFinalOrTransient(field) ) {
                continue;
            }
            // 1.2 Skip ignored fields
            if ((ignoreUnannotated && !field.isAnnotationPresent(ExcelProperty.class))
                    || field.isAnnotationPresent(ExcelIgnore.class)) {
                continue;
            }

            // 2. Core: handle fields annotated with ExcelColumnSelect
            if (field.isAnnotationPresent(ExcelColumnSelect.class)) {
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                if (excelProperty != null && excelProperty.index() != -1) {
                    colIndex = excelProperty.index();
                }
                getSelectDataList(colIndex, field);
            }
            colIndex++;
        }
    }

    /**
     * Check whether the field is static-final or transient.
     * Reason: FastExcel ignores static final or transient fields by default, so we mirror that check here.
     *
     * @param field field
     * @return whether the field is static-final or transient
     */
    private boolean isStaticFinalOrTransient(Field field) {
        return (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                || Modifier.isTransient(field.getModifiers());
    }


    /**
     * Fetch the dropdown data and add it to {@link #selectMap}.
     *
     * @param colIndex column index
     * @param field    field
     */
    private void getSelectDataList(int colIndex, Field field) {
        ExcelColumnSelect columnSelect = field.getAnnotation(ExcelColumnSelect.class);
        String dictType = columnSelect.dictType();
        String functionName = columnSelect.functionName();
        Assert.isTrue(ObjectUtil.isNotEmpty(dictType) || ObjectUtil.isNotEmpty(functionName),
                "Field({}) @ExcelColumnSelect annotation: dictType and functionName must not both be empty", field.getName());

        // Case 1: fetch dropdown data via dictType
        if (StrUtil.isNotEmpty(dictType)) { // Case 1: dictionary data (default)
            selectMap.put(colIndex, DictionaryFrameworkUtils.getDictDataLabelList(dictType));
            return;
        }

        // Case 2: fetch dropdown data via functionName
        Map<String, ExcelColumnSelectFunction> functionMap = SpringUtil.getApplicationContext().getBeansOfType(ExcelColumnSelectFunction.class);
        ExcelColumnSelectFunction function = CollUtil.findOne(functionMap.values(), item -> item.getName().equals(functionName));
        Assert.notNull(function, "no matching function found ({})", functionName);
        selectMap.put(colIndex, function.getOptions());
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (CollUtil.isEmpty(selectMap)) {
            return;
        }

        // 1. Get the relevant handles
        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper(); // Data validation helper for the sheet that needs dropdowns
        Workbook workbook = writeWorkbookHolder.getWorkbook(); // Get the workbook
        List<KeyValue<Integer, List<String>>> keyValues = convertList(selectMap.entrySet(), entry -> new KeyValue<>(entry.getKey(), entry.getValue()));
        keyValues.sort(Comparator.comparing(item -> item.getValue().size())); // Ascending order; otherwise dropdown creation will fail

        // 2. Create the dictionary sheet
        Sheet dictSheet = workbook.createSheet(DICT_SHEET_NAME);
        for (KeyValue<Integer, List<String>> keyValue : keyValues) {
            int rowLength = keyValue.getValue().size();
            // 2.1 Populate the dictionary sheet; each column holds one dictionary entry
            for (int i = 0; i < rowLength; i++) {
                Row row = dictSheet.getRow(i);
                if (row == null) {
                    row = dictSheet.createRow(i);
                }
                row.createCell(keyValue.getKey()).setCellValue(keyValue.getValue().get(i));
            }
            // 2.2 Attach the dropdown to the cell
            setColumnSelect(writeSheetHolder, workbook, helper, keyValue);
        }
    }

    /**
     * Set the dropdown on the cell.
     */
    private static void setColumnSelect(WriteSheetHolder writeSheetHolder, Workbook workbook, DataValidationHelper helper,
                                        KeyValue<Integer, List<String>> keyValue) {
        // 1.1 Create a name that other cells can reference
        Name name = workbook.createName();
        String excelColumn = ExcelUtil.indexToColName(keyValue.getKey());
        // 1.2 Dropdown data source, e.g. dict sheet!$B1:$B2
        String refers = DICT_SHEET_NAME + "!$" + excelColumn + "$1:$" + excelColumn + "$" + keyValue.getValue().size();
        name.setNameName("dict" + keyValue.getKey()); // Set the name
        name.setRefersToFormula(refers); // Set the formula

        // 2.1 Configure the constraint
        DataValidationConstraint constraint = helper.createFormulaListConstraint("dict" + keyValue.getKey()); // Set the reference constraint
        // Set first/last row and first/last column of the dropdown cells
        CellRangeAddressList rangeAddressList = new CellRangeAddressList(FIRST_ROW, LAST_ROW,
                keyValue.getKey(), keyValue.getKey());
        DataValidation validation = helper.createValidation(constraint, rangeAddressList);
        if (validation instanceof HSSFDataValidation) {
            validation.setSuppressDropDownArrow(false);
        } else {
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
        }
        // 2.2 Block values outside the dropdown list
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("Notice", "This value is not in the dropdown selection.");
        // 2.3 Apply the dropdown constraint
        writeSheetHolder.getSheet().addValidationData(validation);
    }

}
