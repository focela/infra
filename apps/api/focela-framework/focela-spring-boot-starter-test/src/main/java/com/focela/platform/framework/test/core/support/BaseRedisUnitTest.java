package com.focela.platform.framework.test.core.support;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.framework.redis.config.FocelaRedisAutoConfiguration;
import com.focela.platform.framework.test.config.RedisTestConfiguration;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit test backed by in-memory Redis.
 *
 * Compared to {@link BaseDbUnitTest}, the in-memory DB is replaced with in-memory Redis.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseRedisUnitTest.Application.class)
@ActiveProfiles("unit-test") // use the application-unit-test config profile
public class BaseRedisUnitTest {

    @Import({
            // Redis config classes
            RedisTestConfiguration.class, // Redis test configuration, used to start RedisServer
            RedisAutoConfiguration.class, // Spring Redis auto-configuration
            FocelaRedisAutoConfiguration.class, // our own Redis config
            RedissonAutoConfiguration.class, // Redisson auto-configuration

            // Other config classes
            SpringUtil.class
    })
    public static class Application {
    }

}
