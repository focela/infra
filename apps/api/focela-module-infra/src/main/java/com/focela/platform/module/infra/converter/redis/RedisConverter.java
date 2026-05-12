package com.focela.platform.module.infra.converter.redis;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.module.infra.controller.admin.redis.dto.RedisMonitorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Properties;

@Mapper
public interface RedisConverter {

    RedisConverter INSTANCE = Mappers.getMapper(RedisConverter.class);

    default RedisMonitorResponse build(Properties info, Long dbSize, Properties commandStats) {
        RedisMonitorResponse response = RedisMonitorResponse.builder().info(info).dbSize(dbSize)
                .commandStats(new ArrayList<>(commandStats.size())).build();
        commandStats.forEach((key, value) -> {
            response.getCommandStats().add(RedisMonitorResponse.CommandStat.builder()
                    .command(StrUtil.subAfter((String) key, "cmdstat_", false))
                    .calls(Long.valueOf(StrUtil.subBetween((String) value, "calls=", ",")))
                    .usec(Long.valueOf(StrUtil.subBetween((String) value, "usec=", ",")))
                    .build());
        });
        return response;
    }

}
