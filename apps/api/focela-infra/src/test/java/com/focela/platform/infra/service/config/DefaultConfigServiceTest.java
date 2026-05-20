package com.focela.platform.infra.service.config;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.test.core.utils.RandomUtils;
import com.focela.platform.infra.controller.admin.config.request.ConfigPageRequest;
import com.focela.platform.infra.controller.admin.config.request.ConfigSaveRequest;
import com.focela.platform.infra.domain.entity.config.ConfigEntity;
import com.focela.platform.infra.repository.mapper.config.ConfigMapper;
import com.focela.platform.infra.enums.config.ConfigTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@Import(DefaultConfigService.class)
public class DefaultConfigServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultConfigService configService;

    @Resource
    private ConfigMapper configMapper;

    @Test
    public void testCreateConfig_success() {
        // Prepare parameters
        ConfigSaveRequest request = randomPojo(ConfigSaveRequest.class)
                .setId(null); // prevent id assignment which would fail the uniqueness check

        // Invoke
        Long configId = configService.createConfig(request);
        // Assert
        assertNotNull(configId);
        // Verify record properties are correct
        ConfigEntity config = configMapper.selectById(configId);
        assertPojoEquals(request, config, "id");
        assertEquals(ConfigTypeEnum.CUSTOM.getType(), config.getType());
    }

    @Test
    public void testUpdateConfig_success() {
        // mock data
        ConfigEntity dbConfig = randomConfigEntity();
        configMapper.insert(dbConfig);// @Sql: first insert an existing record
        // Prepare parameters
        ConfigSaveRequest request = randomPojo(ConfigSaveRequest.class, o -> {
            o.setId(dbConfig.getId()); // set the ID to update
        });

        // Invoke
        configService.updateConfig(request);
        // Verify update is correct
        ConfigEntity config = configMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, config);
    }

    @Test
    public void testDeleteConfig_success() {
        // mock data
        ConfigEntity dbConfig = randomConfigEntity(o -> {
            o.setType(ConfigTypeEnum.CUSTOM.getType()); // only CUSTOM type can be deleted
        });
        configMapper.insert(dbConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbConfig.getId();

        // Invoke
        configService.deleteConfig(id);
        // Verify data no longer exists
        assertNull(configMapper.selectById(id));
    }

    @Test
    public void testDeleteConfig_cannotDeleteSystemType() {
        // mock data
        ConfigEntity dbConfig = randomConfigEntity(o -> {
            o.setType(ConfigTypeEnum.SYSTEM.getType()); // SYSTEM is not allowed to be deleted
        });
        configMapper.insert(dbConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbConfig.getId();

        // Invoke and verify exception
        assertServiceException(() -> configService.deleteConfig(id), CONFIG_SYSTEM_TYPE_CANNOT_BE_DELETED);
    }

    @Test
    public void testValidateConfigExists_success() {
        // mock data
        ConfigEntity dbConfigEntity = randomConfigEntity();
        configMapper.insert(dbConfigEntity);// @Sql: first insert an existing record

        // Invoke; succeeds
        configService.validateConfigExists(dbConfigEntity.getId());
    }

    @Test
    public void testValidateConfigExist_notExists() {
        assertServiceException(() -> configService.validateConfigExists(randomLongId()), CONFIG_NOT_EXISTS);
    }

    @Test
    public void testValidateConfigKeyUnique_success() {
        // Invoke; succeeds
        configService.validateConfigKeyUnique(randomLongId(), randomString());
    }

    @Test
    public void testValidateConfigKeyUnique_keyDuplicateForCreate() {
        // Prepare parameters
        String key = randomString();
        // mock data
        configMapper.insert(randomConfigEntity(o -> o.setConfigKey(key)));

        // Invoke and verify exception
        assertServiceException(() -> configService.validateConfigKeyUnique(null, key),
                CONFIG_KEY_DUPLICATE);
    }

    @Test
    public void testValidateConfigKeyUnique_keyDuplicateForUpdate() {
        // Prepare parameters
        Long id = randomLongId();
        String key = randomString();
        // mock data
        configMapper.insert(randomConfigEntity(o -> o.setConfigKey(key)));

        // Invoke and verify exception
        assertServiceException(() -> configService.validateConfigKeyUnique(id, key),
                CONFIG_KEY_DUPLICATE);
    }

    @Test
    public void testGetConfigPage() {
        // mock data
        ConfigEntity dbConfig = randomConfigEntity(o -> { // will be queried later
            o.setName("focela");
            o.setConfigKey("focela-key");
            o.setType(ConfigTypeEnum.SYSTEM.getType());
            o.setCreateTime(buildTime(2021, 2, 1));
        });
        configMapper.insert(dbConfig);
        // Test name mismatch
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setName("potato")));
        // Test key mismatch
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setConfigKey("potato")));
        // Test type mismatch
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setType(ConfigTypeEnum.CUSTOM.getType())));
        // Test createTime mismatch
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setCreateTime(buildTime(2021, 1, 1))));
        // Prepare parameters
        ConfigPageRequest request = new ConfigPageRequest();
        request.setName("focela");
        request.setKey("focela");
        request.setType(ConfigTypeEnum.SYSTEM.getType());
        request.setCreateTime(buildBetweenTime(2021, 1, 15, 2021, 2, 15));

        // Invoke
        PageResult<ConfigEntity> pageResult = configService.getConfigPage(request);
        // Assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbConfig, pageResult.getList().get(0));
    }

    @Test
    public void testGetConfig() {
        // mock data
        ConfigEntity dbConfig = randomConfigEntity();
        configMapper.insert(dbConfig);// @Sql: first insert an existing record
        // Prepare parameters
        Long id = dbConfig.getId();

        // Invoke
        ConfigEntity config = configService.getConfig(id);
        // Assert
        assertNotNull(config);
        assertPojoEquals(dbConfig, config);
    }

    @Test
    public void testGetConfigByKey() {
        // mock data
        ConfigEntity dbConfig = randomConfigEntity();
        configMapper.insert(dbConfig);// @Sql: first insert an existing record
        // Prepare parameters
        String key = dbConfig.getConfigKey();

        // Invoke
        ConfigEntity config = configService.getConfigByKey(key);
        // Assert
        assertNotNull(config);
        assertPojoEquals(dbConfig, config);
    }

    // ========== Random objects ==========

    @SafeVarargs
    private static ConfigEntity randomConfigEntity(Consumer<ConfigEntity>... consumers) {
        Consumer<ConfigEntity> consumer = (o) -> {
            o.setType(randomEle(ConfigTypeEnum.values()).getType()); // keep the key range constrained
        };
        return RandomUtils.randomPojo(ConfigEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
