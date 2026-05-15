package com.focela.platform.test.core.support;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.datasource.config.FocelaDataSourceAutoConfiguration;
import com.focela.platform.mybatis.config.FocelaMybatisAutoConfiguration;
import com.focela.platform.redis.config.FocelaRedisAutoConfiguration;
import com.focela.platform.test.config.RedisTestConfiguration;
import com.focela.platform.test.config.SqlInitializationTestConfiguration;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * Unit test backed by in-memory DB + Redis.
 *
 * Compared to {@link BaseDbUnitTest}, this additionally adds an in-memory Redis.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseDbAndRedisUnitTest.Application.class)
@ActiveProfiles("unit-test") // use the application-unit-test config profile
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // clean the DB after each unit test
public class BaseDbAndRedisUnitTest {

    @Import({
            // DB config classes
            FocelaDataSourceAutoConfiguration.class, // our own DB config
            DataSourceAutoConfiguration.class, // Spring DB auto-configuration
            DataSourceTransactionManagerAutoConfiguration.class, // Spring transaction auto-configuration
            DruidDataSourceAutoConfigure.class, // Druid auto-configuration
            SqlInitializationTestConfiguration.class, // SQL initialization
            // MyBatis config classes
            FocelaMybatisAutoConfiguration.class, // our own MyBatis config
            MybatisPlusAutoConfiguration.class, // MyBatis auto-configuration

            // Redis config classes
            RedisTestConfiguration.class, // Redis test configuration, used to start RedisServer
            FocelaRedisAutoConfiguration.class, // our own Redis config
            RedisAutoConfiguration.class, // Spring Redis auto-configuration
            RedissonAutoConfiguration.class, // Redisson auto-configuration

            // Other config classes
            SpringUtil.class
    })
    public static class Application {
    }

}
