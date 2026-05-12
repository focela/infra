package com.focela.platform.framework.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(description="page params")
@Data
public class PageParam implements Serializable {

    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    /**
     * 每页条数 - 不分页
     *
     * 例如说，导出接口，可以设置 {@link #pageSize} 为 -1 不分页，查询所有数据。
     */
    public static final Integer PAGE_SIZE_NONE = -1;

    @Schema(description = "page number, slave 1 start", requiredMode = Schema.RequiredMode.REQUIRED,example = "1")
    @NotNull(message = "页码must not be blank")
    @Min(value = 1, message = "页码min value is 1")
    private Integer pageNo = PAGE_NO;

    @Schema(description = "page size, max value as 200", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "page size must not be blank")
    @Min(value = 1, message = "page size min value is 1")
    @Max(value = 200, message = "page size max value is 200")
    private Integer pageSize = PAGE_SIZE;

}
