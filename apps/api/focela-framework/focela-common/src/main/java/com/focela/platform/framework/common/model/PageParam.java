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
     * Page size for "no pagination".
     *
     * For example, an export endpoint can set {@link #pageSize} to -1 to disable pagination and return all data.
     */
    public static final Integer PAGE_SIZE_NONE = -1;

    @Schema(description = "page number, starting from 1", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "page number must not be blank")
    @Min(value = 1, message = "page number min value is 1")
    private Integer pageNo = PAGE_NO;

    @Schema(description = "page size, max value 200", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "page size must not be blank")
    @Min(value = 1, message = "page size min value is 1")
    @Max(value = 200, message = "page size max value is 200")
    private Integer pageSize = PAGE_SIZE;

}
