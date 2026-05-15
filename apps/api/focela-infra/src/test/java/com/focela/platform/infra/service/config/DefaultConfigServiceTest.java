package com.focela.platform.infra.service.config;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.collection.ArrayUtils;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.framework.test.core.utils.RandomUtils;
import com.focela.platform.infra.controller.admin.config.dto.ConfigPageRequest;
import com.focela.platform.infra.controller.admin.config.dto.ConfigSaveRequest;
import com.focela.platform.infra.entity.config.ConfigEntity;
import com.focela.platform.infra.repository.mapper.config.ConfigMapper;
import com.focela.platform.infra.enums.config.ConfigTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.infra.constants.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@Import(DefaultConfigService.class)
public class DefaultConfigServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultConfigService configService;

    @Resource
    private ConfigMapper configMapper;

    @Test
    public void testCreateConfig_success() {
        // 准备参数
        ConfigSaveRequest request = randomPojo(ConfigSaveRequest.class)
                .setId(null); // 防止 id 被赋值，导致唯一性校验失败

        // 调用
        Long configId = configService.createConfig(request);
        // 断言
        assertNotNull(configId);
        // 校验记录的属性是否正确
        ConfigEntity config = configMapper.selectById(configId);
        assertPojoEquals(request, config, "id");
        assertEquals(ConfigTypeEnum.CUSTOM.getType(), config.getType());
    }

    @Test
    public void testUpdateConfig_success() {
        // mock 数据
        ConfigEntity dbConfig = randomConfigDO();
        configMapper.insert(dbConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ConfigSaveRequest request = randomPojo(ConfigSaveRequest.class, o -> {
            o.setId(dbConfig.getId()); // 设置更新的 ID
        });

        // 调用
        configService.updateConfig(request);
        // 校验是否更新正确
        ConfigEntity config = configMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, config);
    }

    @Test
    public void testDeleteConfig_success() {
        // mock 数据
        ConfigEntity dbConfig = randomConfigDO(o -> {
            o.setType(ConfigTypeEnum.CUSTOM.getType()); // 只能删除 CUSTOM 类型
        });
        configMapper.insert(dbConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbConfig.getId();

        // 调用
        configService.deleteConfig(id);
        // 校验数据不存在了
        assertNull(configMapper.selectById(id));
    }

    @Test
    public void testDeleteConfig_canNotDeleteSystemType() {
        // mock 数据
        ConfigEntity dbConfig = randomConfigDO(o -> {
            o.setType(ConfigTypeEnum.SYSTEM.getType()); // SYSTEM 不允许删除
        });
        configMapper.insert(dbConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbConfig.getId();

        // 调用, 并断言异常
        assertServiceException(() -> configService.deleteConfig(id), CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE);
    }

    @Test
    public void testValidateConfigExists_success() {
        // mock 数据
        ConfigEntity dbConfigDO = randomConfigDO();
        configMapper.insert(dbConfigDO);// @Sql: 先插入出一条存在的数据

        // 调用成功
        configService.validateConfigExists(dbConfigDO.getId());
    }

    @Test
    public void testValidateConfigExist_notExists() {
        assertServiceException(() -> configService.validateConfigExists(randomLongId()), CONFIG_NOT_EXISTS);
    }

    @Test
    public void testValidateConfigKeyUnique_success() {
        // 调用，成功
        configService.validateConfigKeyUnique(randomLongId(), randomString());
    }

    @Test
    public void testValidateConfigKeyUnique_keyDuplicateForCreate() {
        // 准备参数
        String key = randomString();
        // mock 数据
        configMapper.insert(randomConfigDO(o -> o.setConfigKey(key)));

        // 调用，校验异常
        assertServiceException(() -> configService.validateConfigKeyUnique(null, key),
                CONFIG_KEY_DUPLICATE);
    }

    @Test
    public void testValidateConfigKeyUnique_keyDuplicateForUpdate() {
        // 准备参数
        Long id = randomLongId();
        String key = randomString();
        // mock 数据
        configMapper.insert(randomConfigDO(o -> o.setConfigKey(key)));

        // 调用，校验异常
        assertServiceException(() -> configService.validateConfigKeyUnique(id, key),
                CONFIG_KEY_DUPLICATE);
    }

    @Test
    public void testGetConfigPage() {
        // mock 数据
        ConfigEntity dbConfig = randomConfigDO(o -> { // 等会查询到
            o.setName("芋艿");
            o.setConfigKey("yunai");
            o.setType(ConfigTypeEnum.SYSTEM.getType());
            o.setCreateTime(buildTime(2021, 2, 1));
        });
        configMapper.insert(dbConfig);
        // 测试 name 不匹配
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setName("土豆")));
        // 测试 key 不匹配
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setConfigKey("tudou")));
        // 测试 type 不匹配
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setType(ConfigTypeEnum.CUSTOM.getType())));
        // 测试 createTime 不匹配
        configMapper.insert(cloneIgnoreId(dbConfig, o -> o.setCreateTime(buildTime(2021, 1, 1))));
        // 准备参数
        ConfigPageRequest request = new ConfigPageRequest();
        request.setName("艿");
        request.setKey("nai");
        request.setType(ConfigTypeEnum.SYSTEM.getType());
        request.setCreateTime(buildBetweenTime(2021, 1, 15, 2021, 2, 15));

        // 调用
        PageResult<ConfigEntity> pageResult = configService.getConfigPage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbConfig, pageResult.getList().get(0));
    }

    @Test
    public void testGetConfig() {
        // mock 数据
        ConfigEntity dbConfig = randomConfigDO();
        configMapper.insert(dbConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbConfig.getId();

        // 调用
        ConfigEntity config = configService.getConfig(id);
        // 断言
        assertNotNull(config);
        assertPojoEquals(dbConfig, config);
    }

    @Test
    public void testGetConfigByKey() {
        // mock 数据
        ConfigEntity dbConfig = randomConfigDO();
        configMapper.insert(dbConfig);// @Sql: 先插入出一条存在的数据
        // 准备参数
        String key = dbConfig.getConfigKey();

        // 调用
        ConfigEntity config = configService.getConfigByKey(key);
        // 断言
        assertNotNull(config);
        assertPojoEquals(dbConfig, config);
    }

    // ========== 随机对象 ==========

    @SafeVarargs
    private static ConfigEntity randomConfigDO(Consumer<ConfigEntity>... consumers) {
        Consumer<ConfigEntity> consumer = (o) -> {
            o.setType(randomEle(ConfigTypeEnum.values()).getType()); // 保证 key 的范围
        };
        return RandomUtils.randomPojo(ConfigEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
