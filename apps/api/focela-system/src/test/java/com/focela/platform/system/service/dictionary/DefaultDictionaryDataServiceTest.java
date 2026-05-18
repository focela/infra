package com.focela.platform.system.service.dictionary;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
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

import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
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
        // mock data
        DictionaryDataEntity dictDataEntity01 = randomDictDataEntity().setDictType("yunai").setSort(2)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictDataMapper.insert(dictDataEntity01);
        DictionaryDataEntity dictDataEntity02 = randomDictDataEntity().setDictType("yunai").setSort(1)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictDataMapper.insert(dictDataEntity02);
        DictionaryDataEntity dictDataEntity03 = randomDictDataEntity().setDictType("yunai").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictDataMapper.insert(dictDataEntity03);
        DictionaryDataEntity dictDataEntity04 = randomDictDataEntity().setDictType("yunai2").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictDataMapper.insert(dictDataEntity04);
        // prepare parameters
        Integer status = CommonStatusEnum.ENABLE.getStatus();
        String dictType = "yunai";

        // invoke
        List<DictionaryDataEntity> dictionaryDataEntities = dictDataService.getDictDataList(status, dictType);
        // assert
        assertEquals(2, dictionaryDataEntities.size());
        assertPojoEquals(dictDataEntity02, dictionaryDataEntities.get(0));
        assertPojoEquals(dictDataEntity01, dictionaryDataEntities.get(1));
    }

    @Test
    public void testGetDictDataPage() {
        // mock data
        DictionaryDataEntity dbDictData = randomPojo(DictionaryDataEntity.class, o -> { // will be queried later
            o.setLabel("Focela");
            o.setDictType("yunai");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        dictDataMapper.insert(dbDictData);
        // test label mismatch
        dictDataMapper.insert(cloneIgnoreId(dbDictData, o -> o.setLabel("Other")));
        // test dictType mismatch
        dictDataMapper.insert(cloneIgnoreId(dbDictData, o -> o.setDictType("nai")));
        // test status mismatch
        dictDataMapper.insert(cloneIgnoreId(dbDictData, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // prepare parameters
        DictionaryDataPageRequest request = new DictionaryDataPageRequest();
        request.setLabel("Focela");
        request.setDictType("yunai");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        PageResult<DictionaryDataEntity> pageResult = dictDataService.getDictDataPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbDictData, pageResult.getList().get(0));
    }

    @Test
    public void testGetDictData() {
        // mock data
        DictionaryDataEntity dbDictData = randomDictDataEntity();
        dictDataMapper.insert(dbDictData);
        // prepare parameters
        Long id = dbDictData.getId();

        // invoke
        DictionaryDataEntity dictData = dictDataService.getDictData(id);
        // assert
        assertPojoEquals(dbDictData, dictData);
    }

    @Test
    public void testCreateDictData_success() {
        // prepare parameters
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // prevent id from being assigned
        // mock the method
        when(dictTypeService.getDictType(eq(request.getDictType()))).thenReturn(randomDictTypeEntity(request.getDictType()));

        // invoke
        Long dictDataId = dictDataService.createDictData(request);
        // assert
        assertNotNull(dictDataId);
        // verify record properties are correct
        DictionaryDataEntity dictData = dictDataMapper.selectById(dictDataId);
        assertPojoEquals(request, dictData, "id");
    }

    @Test
    public void testUpdateDictData_success() {
        // mock data
        DictionaryDataEntity dbDictData = randomDictDataEntity();
        dictDataMapper.insert(dbDictData);// @Sql: first insert an existing record
        // prepare parameters
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class, o -> {
            o.setId(dbDictData.getId()); // set updated ID
            o.setStatus(randomCommonStatus());
        });
        // mock the method, dictionary type
        when(dictTypeService.getDictType(eq(request.getDictType()))).thenReturn(randomDictTypeEntity(request.getDictType()));

        // invoke
        dictDataService.updateDictData(request);
        // verify update is correct
        DictionaryDataEntity dictData = dictDataMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, dictData);
    }

    @Test
    public void testDeleteDictData_success() {
        // mock data
        DictionaryDataEntity dbDictData = randomDictDataEntity();
        dictDataMapper.insert(dbDictData);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictData.getId();

        // invoke
        dictDataService.deleteDictData(id);
        // verify data no longer exists
        assertNull(dictDataMapper.selectById(id));
    }

    @Test
    public void testValidateDictDataExists_success() {
        // mock data
        DictionaryDataEntity dbDictData = randomDictDataEntity();
        dictDataMapper.insert(dbDictData);// @Sql: first insert an existing record

        // invoke succeeded
        dictDataService.validateDictDataExists(dbDictData.getId());
    }

    @Test
    public void testValidateDictDataExists_notExists() {
        assertServiceException(() -> dictDataService.validateDictDataExists(randomLongId()), DICT_DATA_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeExists_success() {
        // mock the method, data type is disabled
        String type = randomString();
        when(dictTypeService.getDictType(eq(type))).thenReturn(randomDictTypeEntity(type));

        // invoke, succeeded
        dictDataService.validateDictTypeExists(type);
    }

    @Test
    public void testValidateDictTypeExists_notExists() {
        assertServiceException(() -> dictDataService.validateDictTypeExists(randomString()), DICT_TYPE_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeExists_notEnable() {
        // mock the method, data type is disabled
        String dictType = randomString();
        when(dictTypeService.getDictType(eq(dictType))).thenReturn(
                randomPojo(DictionaryTypeEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));

        // invoke and assert exception
        assertServiceException(() -> dictDataService.validateDictTypeExists(dictType), DICT_TYPE_NOT_ENABLE);
    }

    @Test
    public void testValidateDictDataValueUnique_success() {
        // invoke, succeeded
        dictDataService.validateDictDataValueUnique(randomLongId(), randomString(), randomString());
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForCreate() {
        // prepare parameters
        String dictType = randomString();
        String value = randomString();
        // mock data
        dictDataMapper.insert(randomDictDataEntity(o -> {
            o.setDictType(dictType);
            o.setValue(value);
        }));

        // invoke, verify exception
        assertServiceException(() -> dictDataService.validateDictDataValueUnique(null, dictType, value),
                DICT_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String dictType = randomString();
        String value = randomString();
        // mock data
        dictDataMapper.insert(randomDictDataEntity(o -> {
            o.setDictType(dictType);
            o.setValue(value);
        }));

        // invoke, verify exception
        assertServiceException(() -> dictDataService.validateDictDataValueUnique(id, dictType, value),
                DICT_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void testGetDictDataCountByDictType() {
        // mock data
        dictDataMapper.insert(randomDictDataEntity(o -> o.setDictType("yunai")));
        dictDataMapper.insert(randomDictDataEntity(o -> o.setDictType("tudou")));
        dictDataMapper.insert(randomDictDataEntity(o -> o.setDictType("yunai")));
        // prepare parameters
        String dictType = "yunai";

        // invoke
        long count = dictDataService.getDictDataCountByDictType(dictType);
        // verify
        assertEquals(2L, count);
    }

    @Test
    public void testValidateDictDataList_success() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictDataMapper.insert(dictDataEntity);
        // prepare parameters
        String dictType = dictDataEntity.getDictType();
        List<String> values = singletonList(dictDataEntity.getValue());

        // invoke, no assertion needed
        dictDataService.validateDictDataList(dictType, values);
    }

    @Test
    public void testValidateDictDataList_notFound() {
        // prepare parameters
        String dictType = randomString();
        List<String> values = singletonList(randomString());

        // invoke and assert exception
        assertServiceException(() -> dictDataService.validateDictDataList(dictType, values), DICT_DATA_NOT_EXISTS);
    }

    @Test
    public void testValidateDictDataList_notEnable() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictDataMapper.insert(dictDataEntity);
        // prepare parameters
        String dictType = dictDataEntity.getDictType();
        List<String> values = singletonList(dictDataEntity.getValue());

        // invoke and assert exception
        assertServiceException(() -> dictDataService.validateDictDataList(dictType, values),
                DICT_DATA_NOT_ENABLE, dictDataEntity.getLabel());
    }

    @Test
    public void testGetDictData_dictType() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setDictType("yunai").setValue("1");
        dictDataMapper.insert(dictDataEntity);
        DictionaryDataEntity dictDataEntity02 = randomDictDataEntity().setDictType("yunai").setValue("2");
        dictDataMapper.insert(dictDataEntity02);
        // prepare parameters
        String dictType = "yunai";
        String value = "1";

        // invoke
        DictionaryDataEntity dbDictData = dictDataService.getDictData(dictType, value);
        // assert
        assertEquals(dictDataEntity, dbDictData);
    }

    @Test
    public void testParseDictData() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setDictType("yunai").setLabel("1");
        dictDataMapper.insert(dictDataEntity);
        DictionaryDataEntity dictDataEntity02 = randomDictDataEntity().setDictType("yunai").setLabel("2");
        dictDataMapper.insert(dictDataEntity02);
        // prepare parameters
        String dictType = "yunai";
        String label = "1";

        // invoke
        DictionaryDataEntity dbDictData = dictDataService.parseDictData(dictType, label);
        // assert
        assertEquals(dictDataEntity, dbDictData);
    }

    // ========== random object ==========

    @SafeVarargs
    private static DictionaryDataEntity randomDictDataEntity(Consumer<DictionaryDataEntity>... consumers) {
        Consumer<DictionaryDataEntity> consumer = (o) -> {
            o.setStatus(randomCommonStatus()); // ensure status range
        };
        return randomPojo(DictionaryDataEntity.class, ArrayUtils.append(consumer, consumers));
    }

    /**
     * generate a valid dictionary type
     *
     * @param type dictionary type
     * @return DictionaryTypeEntity object
     */
    private static DictionaryTypeEntity randomDictTypeEntity(String type) {
        return randomPojo(DictionaryTypeEntity.class, o -> {
            o.setType(type);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // ensure status is enabled
        });
    }

}
