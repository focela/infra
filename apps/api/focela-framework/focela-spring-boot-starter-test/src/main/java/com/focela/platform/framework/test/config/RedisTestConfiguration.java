package com.focela.platform.framework.test.config;

import com.github.fppt.jedismock.RedisServer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

/**
 * Redis test Configuration; mainly starts the embedded Redis.
 */
@Configuration(proxyBeanMethods = false)
@Lazy(false) // disable lazy initialization
@EnableConfigurationProperties(RedisProperties.class)
public class RedisTestConfiguration {

    /**
     * Create the mock Redis Server.
     */
    @Bean
    public RedisServer redisServer(RedisProperties properties) throws IOException {
        RedisServer redisServer = new RedisServer(properties.getPort());
        // When running multiple unit tests, multiple Spring containers may be created and the server is not stopped,
        // which causes the port to be occupied and prevents startup.
        try {
            redisServer.start();
        } catch (Exception ignore) {}
        return redisServer;
    }

}
