package com.focela.platform.framework.test.core.support;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.framework.datasource.config.FocelaDataSourceAutoConfiguration;
import com.focela.platform.framework.mybatis.config.FocelaMybatisAutoConfiguration;
import com.focela.platform.framework.test.config.SqlInitializationTestConfiguration;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * Unit test backed by in-memory DB.
 *
 * Note: this also applies to the Service layer. For Service-layer unit tests, our own module's Mapper uses the H2
 * in-memory database, while other modules' Services are accessed via mock methods.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseDbUnitTest.Application.class)
@ActiveProfiles("unit-test") // use the application-unit-test config profile
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // clean the DB after each unit test
public class BaseDbUnitTest {

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
            MybatisPlusJoinAutoConfiguration.class, // MyBatis Join configuration

            // Other config classes
            SpringUtil.class
    })
    public static class Application {
    }

}
