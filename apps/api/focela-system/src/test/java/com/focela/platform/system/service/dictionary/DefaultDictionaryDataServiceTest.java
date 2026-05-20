package com.focela.platform.system.service.dictionary;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataPageRequest;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataSaveRequest;
import com.focela.platform.system.domain.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.domain.entity.dictionary.DictionaryTypeEntity;
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
    private DefaultDictionaryDataService dictionaryDataService;

    @Resource
    private DictionaryDataMapper dictionaryDataMapper;
    @MockitoBean
    private DictionaryTypeService dictionaryTypeService;

    @Test
    public void testGetDictDataList() {
        // mock data
        DictionaryDataEntity dictDataEntity01 = randomDictDataEntity().setDictType("yunai").setSort(2)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictionaryDataMapper.insert(dictDataEntity01);
        DictionaryDataEntity dictDataEntity02 = randomDictDataEntity().setDictType("yunai").setSort(1)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictionaryDataMapper.insert(dictDataEntity02);
        DictionaryDataEntity dictDataEntity03 = randomDictDataEntity().setDictType("yunai").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictionaryDataMapper.insert(dictDataEntity03);
        DictionaryDataEntity dictDataEntity04 = randomDictDataEntity().setDictType("yunai2").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictionaryDataMapper.insert(dictDataEntity04);
        // prepare parameters
        Integer status = CommonStatusEnum.ENABLE.getStatus();
        String dictionaryType = "yunai";

        // invoke
        List<DictionaryDataEntity> dictionaryDataEntities = dictionaryDataService.getDictDataList(status, dictionaryType);
        // assert
        assertEquals(2, dictionaryDataEntities.size());
        assertPojoEquals(dictDataEntity02, dictionaryDataEntities.get(0));
        assertPojoEquals(dictDataEntity01, dictionaryDataEntities.get(1));
    }

    @Test
    public void testGetDictDataPage() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomPojo(DictionaryDataEntity.class, o -> { // will be queried later
            o.setLabel("Focela");
            o.setDictType("yunai");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
        });
        dictionaryDataMapper.insert(dbDictionaryData);
        // test label mismatch
        dictionaryDataMapper.insert(cloneIgnoreId(dbDictionaryData, o -> o.setLabel("Other")));
        // test dictionaryType mismatch
        dictionaryDataMapper.insert(cloneIgnoreId(dbDictionaryData, o -> o.setDictType("nai")));
        // test status mismatch
        dictionaryDataMapper.insert(cloneIgnoreId(dbDictionaryData, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // prepare parameters
        DictionaryDataPageRequest request = new DictionaryDataPageRequest();
        request.setLabel("Focela");
        request.setDictType("yunai");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        PageResult<DictionaryDataEntity> pageResult = dictionaryDataService.getDictDataPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbDictionaryData, pageResult.getList().get(0));
    }

    @Test
    public void testGetDictData() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);
        // prepare parameters
        Long id = dbDictionaryData.getId();

        // invoke
        DictionaryDataEntity dictionaryData = dictionaryDataService.getDictData(id);
        // assert
        assertPojoEquals(dbDictionaryData, dictionaryData);
    }

    @Test
    public void testCreateDictData_success() {
        // prepare parameters
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // prevent id from being assigned
        // mock the method
        when(dictionaryTypeService.getDictType(eq(request.getDictType()))).thenReturn(randomDictTypeEntity(request.getDictType()));

        // invoke
        Long dictionaryDataId = dictionaryDataService.createDictData(request);
        // assert
        assertNotNull(dictionaryDataId);
        // verify record properties are correct
        DictionaryDataEntity dictionaryData = dictionaryDataMapper.selectById(dictionaryDataId);
        assertPojoEquals(request, dictionaryData, "id");
    }

    @Test
    public void testUpdateDictData_success() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);// @Sql: first insert an existing record
        // prepare parameters
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class, o -> {
            o.setId(dbDictionaryData.getId()); // set updated ID
            o.setStatus(randomCommonStatus());
        });
        // mock the method, dictionary type
        when(dictionaryTypeService.getDictType(eq(request.getDictType()))).thenReturn(randomDictTypeEntity(request.getDictType()));

        // invoke
        dictionaryDataService.updateDictData(request);
        // verify update is correct
        DictionaryDataEntity dictionaryData = dictionaryDataMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, dictionaryData);
    }

    @Test
    public void testDeleteDictData_success() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictionaryData.getId();

        // invoke
        dictionaryDataService.deleteDictData(id);
        // verify data no longer exists
        assertNull(dictionaryDataMapper.selectById(id));
    }

    @Test
    public void testValidateDictDataExists_success() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);// @Sql: first insert an existing record

        // invoke succeeded
        dictionaryDataService.validateDictDataExists(dbDictionaryData.getId());
    }

    @Test
    public void testValidateDictDataExists_notExists() {
        assertServiceException(() -> dictionaryDataService.validateDictDataExists(randomLongId()), DICT_DATA_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeExists_success() {
        // mock the method, data type is disabled
        String type = randomString();
        when(dictionaryTypeService.getDictType(eq(type))).thenReturn(randomDictTypeEntity(type));

        // invoke, succeeded
        dictionaryDataService.validateDictTypeExists(type);
    }

    @Test
    public void testValidateDictTypeExists_notExists() {
        assertServiceException(() -> dictionaryDataService.validateDictTypeExists(randomString()), DICT_TYPE_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeExists_notEnable() {
        // mock the method, data type is disabled
        String dictionaryType = randomString();
        when(dictionaryTypeService.getDictType(eq(dictionaryType))).thenReturn(
                randomPojo(DictionaryTypeEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));

        // invoke and assert exception
        assertServiceException(() -> dictionaryDataService.validateDictTypeExists(dictionaryType), DICT_TYPE_NOT_ENABLE);
    }

    @Test
    public void testValidateDictDataValueUnique_success() {
        // invoke, succeeded
        dictionaryDataService.validateDictDataValueUnique(randomLongId(), randomString(), randomString());
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForCreate() {
        // prepare parameters
        String dictionaryType = randomString();
        String value = randomString();
        // mock data
        dictionaryDataMapper.insert(randomDictDataEntity(o -> {
            o.setDictType(dictionaryType);
            o.setValue(value);
        }));

        // invoke, verify exception
        assertServiceException(() -> dictionaryDataService.validateDictDataValueUnique(null, dictionaryType, value),
                DICT_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String dictionaryType = randomString();
        String value = randomString();
        // mock data
        dictionaryDataMapper.insert(randomDictDataEntity(o -> {
            o.setDictType(dictionaryType);
            o.setValue(value);
        }));

        // invoke, verify exception
        assertServiceException(() -> dictionaryDataService.validateDictDataValueUnique(id, dictionaryType, value),
                DICT_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void testGetDictDataCountByDictType() {
        // mock data
        dictionaryDataMapper.insert(randomDictDataEntity(o -> o.setDictType("yunai")));
        dictionaryDataMapper.insert(randomDictDataEntity(o -> o.setDictType("tudou")));
        dictionaryDataMapper.insert(randomDictDataEntity(o -> o.setDictType("yunai")));
        // prepare parameters
        String dictionaryType = "yunai";

        // invoke
        long count = dictionaryDataService.getDictDataCountByDictType(dictionaryType);
        // verify
        assertEquals(2L, count);
    }

    @Test
    public void testValidateDictDataList_success() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictionaryDataMapper.insert(dictDataEntity);
        // prepare parameters
        String dictionaryType = dictDataEntity.getDictType();
        List<String> values = singletonList(dictDataEntity.getValue());

        // invoke, no assertion needed
        dictionaryDataService.validateDictDataList(dictionaryType, values);
    }

    @Test
    public void testValidateDictDataList_notFound() {
        // prepare parameters
        String dictionaryType = randomString();
        List<String> values = singletonList(randomString());

        // invoke and assert exception
        assertServiceException(() -> dictionaryDataService.validateDictDataList(dictionaryType, values), DICT_DATA_NOT_EXISTS);
    }

    @Test
    public void testValidateDictDataList_notEnable() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictionaryDataMapper.insert(dictDataEntity);
        // prepare parameters
        String dictionaryType = dictDataEntity.getDictType();
        List<String> values = singletonList(dictDataEntity.getValue());

        // invoke and assert exception
        assertServiceException(() -> dictionaryDataService.validateDictDataList(dictionaryType, values),
                DICT_DATA_NOT_ENABLE, dictDataEntity.getLabel());
    }

    @Test
    public void testGetDictData_dictType() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setDictType("yunai").setValue("1");
        dictionaryDataMapper.insert(dictDataEntity);
        DictionaryDataEntity dictDataEntity02 = randomDictDataEntity().setDictType("yunai").setValue("2");
        dictionaryDataMapper.insert(dictDataEntity02);
        // prepare parameters
        String dictionaryType = "yunai";
        String value = "1";

        // invoke
        DictionaryDataEntity dbDictionaryData = dictionaryDataService.getDictData(dictionaryType, value);
        // assert
        assertEquals(dictDataEntity, dbDictionaryData);
    }

    @Test
    public void testParseDictData() {
        // mock data
        DictionaryDataEntity dictDataEntity = randomDictDataEntity().setDictType("yunai").setLabel("1");
        dictionaryDataMapper.insert(dictDataEntity);
        DictionaryDataEntity dictDataEntity02 = randomDictDataEntity().setDictType("yunai").setLabel("2");
        dictionaryDataMapper.insert(dictDataEntity02);
        // prepare parameters
        String dictionaryType = "yunai";
        String label = "1";

        // invoke
        DictionaryDataEntity dbDictionaryData = dictionaryDataService.parseDictData(dictionaryType, label);
        // assert
        assertEquals(dictDataEntity, dbDictionaryData);
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
