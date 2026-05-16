package com.focela.platform.infra.controller.admin.redis;

import com.focela.platform.common.model.CommonResult;
import com.focela.platform.infra.controller.admin.redis.dto.RedisMonitorResponse;
import com.focela.platform.infra.converter.redis.RedisConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Redis monitor")
@RestController
@RequestMapping("/infra/redis")
@RequiredArgsConstructor
public class RedisController {

    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/get-monitor-info")
    @Operation(summary = "get Redis monitor info")
    @PreAuthorize("@ss.hasPermission('infra:redis:get-monitor-info')")
    public CommonResult<RedisMonitorResponse> getRedisMonitorInfo() {
        // Get Redis statistics
        Properties info = stringRedisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
        Long dbSize = stringRedisTemplate.execute(RedisServerCommands::dbSize);
        Properties commandStats = stringRedisTemplate.execute((
                RedisCallback<Properties>) connection -> connection.serverCommands().info("commandstats"));
        assert commandStats != null; // assert to avoid warnings
        // Assemble result and return
        return success(RedisConverter.INSTANCE.build(info, dbSize, commandStats));
    }

}
