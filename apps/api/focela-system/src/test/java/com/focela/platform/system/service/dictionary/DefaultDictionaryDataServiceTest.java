package com.focela.platform.system.service.dictionary;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.collection.ArrayUtils;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.dictionary.dto.data.DictionaryDataPageRequest;
import com.focela.platform.system.controller.admin.dictionary.dto.data.DictionaryDataSaveRequest;
import com.focela.platform.system.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.system.repository.mapper.dictionary.DictionaryDataMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.function.Consumer;

import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(DefaultDictionaryDataService.class)
public class DefaultDictionaryDataServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultDictionaryDataService dictDataService;

    @Resource
    private DictionaryDataMapper dictDataMapper;
    @MockitoBean
    private DictionaryTypeService dictTypeService;

    @Test
    public void testGetDictDataList() {
        // mock 数据
        DictionaryDataEntity dictDataDO01 = randomDictDataDO().setDictType("yunai").setSort(2)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictDataMapper.insert(dictDataDO01);
        DictionaryDataEntity dictDataDO02 = randomDictDataDO().setDictType("yunai").setSort(1)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictDataMapper.insert(dictDataDO02);
        DictionaryDataEntity dictDataDO03 = randomDictDataDO().setDictType("yunai").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictDataMapper.insert(dictDataDO03);
        DictionaryDataEntity dictDataDO04 = randomDictDataDO().setDictType("yunai2").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictDataMapper.insert(dictDataDO04);
        // 准备参数
        Integer status = CommonStatusEnum.ENABLE.getStatus();
        String dictType = "yunai";

        // 调用
        List<DictionaryDataEntity> dictDataDOList = dictDataService.getDictDataList(status, dictType);
        // 断言
        assertEquals(2, dictDataDOList.size());
        assertPojoEquals(dictDataDO02, dictDataDOList.get(0));
        assertPojoEquals(dictDataDO01, dictDataDOList.get(1));
    }

    @Test
    public void testGetDictDataPage() {
        // mock 数据
        DictionaryDataEntity dbDictData = randomPojo(DictionaryDataEntity.class, o -> { // 等会查询到
            o.setLabel("芋艿");
            o.setDictType("yunai");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        dictDataMapper.insert(dbDictData);
        // 测试 label 不匹配
        dictDataMapper.insert(cloneIgnoreId(dbDictData, o -> o.setLabel("艿")));
        // 测试 dictType 不匹配
        dictDataMapper.insert(cloneIgnoreId(dbDictData, o -> o.setDictType("nai")));
        // 测试 status 不匹配
        dictDataMapper.insert(cloneIgnoreId(dbDictData, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 准备参数
        DictionaryDataPageRequest request = new DictionaryDataPageRequest();
        request.setLabel("芋");
        request.setDictType("yunai");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 调用
        PageResult<DictionaryDataEntity> pageResult = dictDataService.getDictDataPage(request);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbDictData, pageResult.getList().get(0));
    }

    @Test
    public void testGetDictData() {
        // mock 数据
        DictionaryDataEntity dbDictData = randomDictDataDO();
        dictDataMapper.insert(dbDictData);
        // 准备参数
        Long id = dbDictData.getId();

        // 调用
        DictionaryDataEntity dictData = dictDataService.getDictData(id);
        // 断言
        assertPojoEquals(dbDictData, dictData);
    }

    @Test
    public void testCreateDictData_success() {
        // 准备参数
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // 防止 id 被赋值
        // mock 方法
        when(dictTypeService.getDictType(eq(request.getDictType()))).thenReturn(randomDictTypeDO(request.getDictType()));

        // 调用
        Long dictDataId = dictDataService.createDictData(request);
        // 断言
        assertNotNull(dictDataId);
        // 校验记录的属性是否正确
        DictionaryDataEntity dictData = dictDataMapper.selectById(dictDataId);
        assertPojoEquals(request, dictData, "id");
    }

    @Test
    public void testUpdateDictData_success() {
        // mock 数据
        DictionaryDataEntity dbDictData = randomDictDataDO();
        dictDataMapper.insert(dbDictData);// @Sql: 先插入出一条存在的数据
        // 准备参数
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class, o -> {
            o.setId(dbDictData.getId()); // 设置更新的 ID
            o.setStatus(randomCommonStatus());
        });
        // mock 方法，字典类型
        when(dictTypeService.getDictType(eq(request.getDictType()))).thenReturn(randomDictTypeDO(request.getDictType()));

        // 调用
        dictDataService.updateDictData(request);
        // 校验是否更新正确
        DictionaryDataEntity dictData = dictDataMapper.selectById(request.getId()); // 获取最新的
        assertPojoEquals(request, dictData);
    }

    @Test
    public void testDeleteDictData_success() {
        // mock 数据
        DictionaryDataEntity dbDictData = randomDictDataDO();
        dictDataMapper.insert(dbDictData);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbDictData.getId();

        // 调用
        dictDataService.deleteDictData(id);
        // 校验数据不存在了
        assertNull(dictDataMapper.selectById(id));
    }

    @Test
    public void testValidateDictDataExists_success() {
        // mock 数据
        DictionaryDataEntity dbDictData = randomDictDataDO();
        dictDataMapper.insert(dbDictData);// @Sql: 先插入出一条存在的数据

        // 调用成功
        dictDataService.validateDictDataExists(dbDictData.getId());
    }

    @Test
    public void testValidateDictDataExists_notExists() {
        assertServiceException(() -> dictDataService.validateDictDataExists(randomLongId()), DICT_DATA_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeExists_success() {
        // mock 方法，数据类型被禁用
        String type = randomString();
        when(dictTypeService.getDictType(eq(type))).thenReturn(randomDictTypeDO(type));

        // 调用, 成功
        dictDataService.validateDictTypeExists(type);
    }

    @Test
    public void testValidateDictTypeExists_notExists() {
        assertServiceException(() -> dictDataService.validateDictTypeExists(randomString()), DICT_TYPE_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeExists_notEnable() {
        // mock 方法，数据类型被禁用
        String dictType = randomString();
        when(dictTypeService.getDictType(eq(dictType))).thenReturn(
                randomPojo(DictionaryTypeEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));

        // 调用, 并断言异常
        assertServiceException(() -> dictDataService.validateDictTypeExists(dictType), DICT_TYPE_NOT_ENABLE);
    }

    @Test
    public void testValidateDictDataValueUnique_success() {
        // 调用，成功
        dictDataService.validateDictDataValueUnique(randomLongId(), randomString(), randomString());
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForCreate() {
        // 准备参数
        String dictType = randomString();
        String value = randomString();
        // mock 数据
        dictDataMapper.insert(randomDictDataDO(o -> {
            o.setDictType(dictType);
            o.setValue(value);
        }));

        // 调用，校验异常
        assertServiceException(() -> dictDataService.validateDictDataValueUnique(null, dictType, value),
                DICT_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForUpdate() {
        // 准备参数
        Long id = randomLongId();
        String dictType = randomString();
        String value = randomString();
        // mock 数据
        dictDataMapper.insert(randomDictDataDO(o -> {
            o.setDictType(dictType);
            o.setValue(value);
        }));

        // 调用，校验异常
        assertServiceException(() -> dictDataService.validateDictDataValueUnique(id, dictType, value),
                DICT_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void testGetDictDataCountByDictType() {
        // mock 数据
        dictDataMapper.insert(randomDictDataDO(o -> o.setDictType("yunai")));
        dictDataMapper.insert(randomDictDataDO(o -> o.setDictType("tudou")));
        dictDataMapper.insert(randomDictDataDO(o -> o.setDictType("yunai")));
        // 准备参数
        String dictType = "yunai";

        // 调用
        long count = dictDataService.getDictDataCountByDictType(dictType);
        // 校验
        assertEquals(2L, count);
    }

    @Test
    public void testValidateDictDataList_success() {
        // mock 数据
        DictionaryDataEntity dictDataDO = randomDictDataDO().setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictDataMapper.insert(dictDataDO);
        // 准备参数
        String dictType = dictDataDO.getDictType();
        List<String> values = singletonList(dictDataDO.getValue());

        // 调用，无需断言
        dictDataService.validateDictDataList(dictType, values);
    }

    @Test
    public void testValidateDictDataList_notFound() {
        // 准备参数
        String dictType = randomString();
        List<String> values = singletonList(randomString());

        // 调用, 并断言异常
        assertServiceException(() -> dictDataService.validateDictDataList(dictType, values), DICT_DATA_NOT_EXISTS);
    }

    @Test
    public void testValidateDictDataList_notEnable() {
        // mock 数据
        DictionaryDataEntity dictDataDO = randomDictDataDO().setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictDataMapper.insert(dictDataDO);
        // 准备参数
        String dictType = dictDataDO.getDictType();
        List<String> values = singletonList(dictDataDO.getValue());

        // 调用, 并断言异常
        assertServiceException(() -> dictDataService.validateDictDataList(dictType, values),
                DICT_DATA_NOT_ENABLE, dictDataDO.getLabel());
    }

    @Test
    public void testGetDictData_dictType() {
        // mock 数据
        DictionaryDataEntity dictDataDO = randomDictDataDO().setDictType("yunai").setValue("1");
        dictDataMapper.insert(dictDataDO);
        DictionaryDataEntity dictDataDO02 = randomDictDataDO().setDictType("yunai").setValue("2");
        dictDataMapper.insert(dictDataDO02);
        // 准备参数
        String dictType = "yunai";
        String value = "1";

        // 调用
        DictionaryDataEntity dbDictData = dictDataService.getDictData(dictType, value);
        // 断言
        assertEquals(dictDataDO, dbDictData);
    }

    @Test
    public void testParseDictData() {
        // mock 数据
        DictionaryDataEntity dictDataDO = randomDictDataDO().setDictType("yunai").setLabel("1");
        dictDataMapper.insert(dictDataDO);
        DictionaryDataEntity dictDataDO02 = randomDictDataDO().setDictType("yunai").setLabel("2");
        dictDataMapper.insert(dictDataDO02);
        // 准备参数
        String dictType = "yunai";
        String label = "1";

        // 调用
        DictionaryDataEntity dbDictData = dictDataService.parseDictData(dictType, label);
        // 断言
        assertEquals(dictDataDO, dbDictData);
    }

    // ========== 随机对象 ==========

    @SafeVarargs
    private static DictionaryDataEntity randomDictDataDO(Consumer<DictionaryDataEntity>... consumers) {
        Consumer<DictionaryDataEntity> consumer = (o) -> {
            o.setStatus(randomCommonStatus()); // 保证 status 的范围
        };
        return randomPojo(DictionaryDataEntity.class, ArrayUtils.append(consumer, consumers));
    }

    /**
     * 生成一个有效的字典类型
     *
     * @param type 字典类型
     * @return DictionaryTypeEntity 对象
     */
    private static DictionaryTypeEntity randomDictTypeDO(String type) {
        return randomPojo(DictionaryTypeEntity.class, o -> {
            o.setType(type);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 保证 status 是开启
        });
    }

}
