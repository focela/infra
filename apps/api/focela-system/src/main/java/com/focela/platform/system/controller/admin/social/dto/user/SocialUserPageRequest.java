package com.focela.platform.system.controller.admin.social.dto.user;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - social user page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SocialUserPageRequest extends PageParam {

    @Schema(description = "Social platform type", example = "30")
    private Integer type;

    @Schema(description = "Nickname", example = "John Doe")
    private String nickname;

    @Schema(description = "Social openid", example = "oz-Jdt0kd_jdhUxJHQdBJMlOFN7w")
    private String openid;

    @Schema(description = "Created time")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
