package com.focela.platform.mybatis.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.mybatis.core.handler.DefaultDBFieldHandler;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.IJsonTypeHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.incrementer.DmKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.KingbaseKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.parser.cache.JdkSerialCaffeineJsqlParseCache;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MyBatis configuration class
 */
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class) // Purpose: run before MyBatis Plus auto-configuration to avoid @MapperScan possibly missing Mappers and printing warn logs
@MapperScan(value = "${focela.info.base-package}", annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}") // Mapper lazy initialization, currently used only for unit tests
public class FocelaMybatisAutoConfiguration {

    static {
        // Dynamic SQL smart optimization with local-cache-accelerated parsing, better support for complex tenant XML dynamic SQL, statically injected cache
        JsqlParserGlobal.setJsqlParseCache(new JdkSerialCaffeineJsqlParseCache(
                (cache) -> cache.maximumSize(1024)
                        .expireAfterWrite(5, TimeUnit.SECONDS))
        );
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // Pagination plugin
        // ↓↓↓ Enable on demand; may affect places using updateBatch, e.g. file config management ↓↓↓
        // mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor()); // Block unconditional update and delete statements
        return mybatisPlusInterceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {
        return new DefaultDBFieldHandler(); // Auto-fill parameter handler
    }

    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
    public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
        DbType dbType = IdTypeEnvironmentPostProcessor.getDbType(environment);
        if (dbType != null) {
            switch (dbType) {
                case POSTGRE_SQL:
                    return new PostgreKeyGenerator();
                case ORACLE:
                case ORACLE_12C:
                    return new OracleKeyGenerator();
                case H2:
                    return new H2KeyGenerator();
                case KINGBASE_ES:
                    return new KingbaseKeyGenerator();
                case DM:
                    return new DmKeyGenerator();
            }
        }
        // No suitable IKeyGenerator implementation class found
        throw new IllegalArgumentException(StrUtil.format("DbType{} cannot find suitable IKeyGenerator implementation class", dbType));
    }

    @Bean // Note: return type is Object rather than JacksonTypeHandler to avoid JacksonTypeHandler being used globally by MyBatis!
    public Object jacksonTypeHandler(List<ObjectMapper> objectMappers) {
        // Note: set the ObjectMapper for JacksonTypeHandler!
        ObjectMapper objectMapper = CollUtil.getFirst(objectMappers);
        if (objectMapper == null) {
            objectMapper = JsonUtils.getObjectMapper();
        }
        JacksonTypeHandler.setObjectMapper(objectMapper);
        return new JacksonTypeHandler(Object.class);
    }

}
