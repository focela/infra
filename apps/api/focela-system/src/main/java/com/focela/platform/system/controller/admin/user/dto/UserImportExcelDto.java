package com.focela.platform.system.controller.admin.user.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.focela.platform.excel.core.annotations.DictionaryFormat;
import com.focela.platform.excel.core.converter.DictionaryConverter;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Excel import VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserImportExcelDto {

    @ExcelProperty("Login name")
    private String username;

    @ExcelProperty("Nickname")
    private String nickname;

    @ExcelProperty("Department ID")
    private Long deptId;

    @ExcelProperty("Email")
    private String email;

    @ExcelProperty("Mobile number")
    private String mobile;

    @ExcelProperty(value = "Gender", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.USER_SEX)
    private Integer sex;

    @ExcelProperty(value = "Account status", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

}
