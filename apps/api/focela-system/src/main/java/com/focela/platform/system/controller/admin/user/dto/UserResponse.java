package com.focela.platform.system.controller.admin.user.dto;

import com.focela.platform.framework.excel.core.annotations.DictionaryFormat;
import com.focela.platform.framework.excel.core.converter.DictionaryConverter;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Admin - user info Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserResponse{

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("用户编号")
    private Long id;

    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @ExcelProperty("用户名称")
    private String username;

    @Schema(description = "Nickname", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @ExcelProperty("用户昵称")
    private String nickname;

    @Schema(description = "Remarks", example = "I am a user")
    private String remark;

    @Schema(description = "Department ID", example = "I am a user")
    private Long deptId;
    @Schema(description = "Department name", example = "IT dept")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "Post ID list", example = "1")
    private Set<Long> postIds;

    @Schema(description = "Email", example = "user@example.com")
    @ExcelProperty("用户邮箱")
    private String email;

    @Schema(description = "Mobile number", example = "15601691300")
    @ExcelProperty("手机号码")
    private String mobile;

    @Schema(description = "Gender, see SexEnum", example = "1")
    @ExcelProperty(value = "用户性别", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.USER_SEX)
    private Integer sex;

    @Schema(description = "Avatar", example = "https://www.example.com/xxx.png")
    private String avatar;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "帐号状态", converter = DictionaryConverter.class)
    @DictionaryFormat(DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(description = "Last login IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "192.168.1.1")
    @ExcelProperty("最后登录IP")
    private String loginIp;

    @Schema(description = "Last login time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    @ExcelProperty("最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED, example = "timestamp format")
    private LocalDateTime createTime;

}
