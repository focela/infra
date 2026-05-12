package com.focela.platform.module.system.controller.admin.sms.dto.log;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - SMS log page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsLogPageRequest extends PageParam {

    @Schema(description = "SMS channel ID", example = "10")
    private Long channelId;

    @Schema(description = "Template ID", example = "20")
    private Long templateId;

    @Schema(description = "Mobile number", example = "15601691300")
    private String mobile;

    @Schema(description = "send status, see SmsSendStatusEnum enum", example = "1")
    private Integer sendStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Sent time")
    private LocalDateTime[] sendTime;

    @Schema(description = "receive status, see SmsReceiveStatusEnum enum", example = "0")
    private Integer receiveStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Received time")
    private LocalDateTime[] receiveTime;

}
