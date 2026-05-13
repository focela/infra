package com.focela.platform.module.infra.controller.admin.redis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Properties;

@Schema(description = "Admin - Redis monitor info Response VO")
@Data
@Builder
@AllArgsConstructor
public class RedisMonitorResponse {

    @Schema(description = "Redis info command result,specific fields, view Redis docs", requiredMode = Schema.RequiredMode.REQUIRED)
    private Properties info;

    @Schema(description = "Redis key count", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long dbSize;

    @Schema(description = "CommandStat array", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CommandStat> commandStats;

    @Schema(description = "Redis command stats result")
    @Data
    @Builder
    @AllArgsConstructor
    public static class CommandStat {

        @Schema(description = "Redis command", requiredMode = Schema.RequiredMode.REQUIRED, example = "get")
        private String command;

        @Schema(description = "call count", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private Long calls;

        @Schema(description = "consumed CPU seconds count", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
        private Long usec;

    }

}
