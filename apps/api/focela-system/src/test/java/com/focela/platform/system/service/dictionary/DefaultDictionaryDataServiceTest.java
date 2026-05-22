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
    public void getDictionaryDataList() {
        // mock data
        DictionaryDataEntity dictionaryDataEntity01 = randomDictionaryDataEntity().setDictType("focela_sample").setSort(2)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictionaryDataMapper.insert(dictionaryDataEntity01);
        DictionaryDataEntity dictionaryDataEntity02 = randomDictionaryDataEntity().setDictType("focela_sample").setSort(1)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictionaryDataMapper.insert(dictionaryDataEntity02);
        DictionaryDataEntity dictionaryDataEntity03 = randomDictionaryDataEntity().setDictType("focela_sample").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictionaryDataMapper.insert(dictionaryDataEntity03);
        DictionaryDataEntity dictionaryDataEntity04 = randomDictionaryDataEntity().setDictType("focela_sample_alt").setSort(3)
                .setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictionaryDataMapper.insert(dictionaryDataEntity04);
        // prepare parameters
        Integer status = CommonStatusEnum.ENABLE.getStatus();
        String dictionaryType = "focela_sample";

        // invoke
        List<DictionaryDataEntity> dictionaryDataEntities = dictionaryDataService.getDictionaryDataList(status, dictionaryType);
        // assert
        assertEquals(2, dictionaryDataEntities.size());
        assertPojoEquals(dictionaryDataEntity02, dictionaryDataEntities.get(0));
        assertPojoEquals(dictionaryDataEntity01, dictionaryDataEntities.get(1));
    }

    @Test
    public void getDictionaryDataPage() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomPojo(DictionaryDataEntity.class, o -> { // will be queried later
            o.setLabel("Focela");
            o.setDictType("focela_sample");
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
        request.setDictType("focela_sample");
        request.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        PageResult<DictionaryDataEntity> pageResult = dictionaryDataService.getDictionaryDataPage(request);
        // assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbDictionaryData, pageResult.getList().get(0));
    }

    @Test
    public void getDictionaryData() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictionaryDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);
        // prepare parameters
        Long id = dbDictionaryData.getId();

        // invoke
        DictionaryDataEntity dictionaryData = dictionaryDataService.getDictionaryData(id);
        // assert
        assertPojoEquals(dbDictionaryData, dictionaryData);
    }

    @Test
    public void createDictionaryData_success() {
        // prepare parameters
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class,
                o -> o.setStatus(randomCommonStatus()))
                .setId(null); // prevent id from being assigned
        // mock the method
        when(dictionaryTypeService.getDictionaryType(eq(request.getDictType()))).thenReturn(randomDictionaryTypeEntity(request.getDictType()));

        // invoke
        Long dictionaryDataId = dictionaryDataService.createDictionaryData(request);
        // assert
        assertNotNull(dictionaryDataId);
        // verify record properties are correct
        DictionaryDataEntity dictionaryData = dictionaryDataMapper.selectById(dictionaryDataId);
        assertPojoEquals(request, dictionaryData, "id");
    }

    @Test
    public void updateDictionaryData_success() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictionaryDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);// @Sql: first insert an existing record
        // prepare parameters
        DictionaryDataSaveRequest request = randomPojo(DictionaryDataSaveRequest.class, o -> {
            o.setId(dbDictionaryData.getId()); // set updated ID
            o.setStatus(randomCommonStatus());
        });
        // mock the method, dictionary type
        when(dictionaryTypeService.getDictionaryType(eq(request.getDictType()))).thenReturn(randomDictionaryTypeEntity(request.getDictType()));

        // invoke
        dictionaryDataService.updateDictionaryData(request);
        // verify update is correct
        DictionaryDataEntity dictionaryData = dictionaryDataMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, dictionaryData);
    }

    @Test
    public void deleteDictionaryData_success() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictionaryDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictionaryData.getId();

        // invoke
        dictionaryDataService.deleteDictionaryData(id);
        // verify data no longer exists
        assertNull(dictionaryDataMapper.selectById(id));
    }

    @Test
    public void validateDictionaryDataExists_success() {
        // mock data
        DictionaryDataEntity dbDictionaryData = randomDictionaryDataEntity();
        dictionaryDataMapper.insert(dbDictionaryData);// @Sql: first insert an existing record

        // invoke succeeded
        dictionaryDataService.validateDictDataExists(dbDictionaryData.getId());
    }

    @Test
    public void validateDictionaryDataExists_missing() {
        assertServiceException(() -> dictionaryDataService.validateDictDataExists(randomLongId()), DICTIONARY_DATA_NOT_FOUND);
    }

    @Test
    public void validateDictionaryTypeExists_success() {
        // mock the method, data type is disabled
        String type = randomString();
        when(dictionaryTypeService.getDictionaryType(eq(type))).thenReturn(randomDictionaryTypeEntity(type));

        // invoke, succeeded
        dictionaryDataService.validateDictTypeExists(type);
    }

    @Test
    public void validateDictionaryTypeExists_missing() {
        assertServiceException(() -> dictionaryDataService.validateDictTypeExists(randomString()), DICTIONARY_TYPE_NOT_FOUND);
    }

    @Test
    public void validateDictionaryTypeExists_disabled() {
        // mock the method, data type is disabled
        String dictionaryType = randomString();
        when(dictionaryTypeService.getDictionaryType(eq(dictionaryType))).thenReturn(
                randomPojo(DictionaryTypeEntity.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));

        // invoke and assert exception
        assertServiceException(() -> dictionaryDataService.validateDictTypeExists(dictionaryType), DICTIONARY_TYPE_NOT_ENABLED);
    }

    @Test
    public void validateDictionaryDataValueUnique_success() {
        // invoke, succeeded
        dictionaryDataService.validateDictDataValueUnique(randomLongId(), randomString(), randomString());
    }

    @Test
    public void validateDictionaryDataValueUnique_valueDuplicateForCreate() {
        // prepare parameters
        String dictionaryType = randomString();
        String value = randomString();
        // mock data
        dictionaryDataMapper.insert(randomDictionaryDataEntity(o -> {
            o.setDictType(dictionaryType);
            o.setValue(value);
        }));

        // invoke, verify exception
        assertServiceException(() -> dictionaryDataService.validateDictDataValueUnique(null, dictionaryType, value),
                DICTIONARY_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void validateDictionaryDataValueUnique_valueDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String dictionaryType = randomString();
        String value = randomString();
        // mock data
        dictionaryDataMapper.insert(randomDictionaryDataEntity(o -> {
            o.setDictType(dictionaryType);
            o.setValue(value);
        }));

        // invoke, verify exception
        assertServiceException(() -> dictionaryDataService.validateDictDataValueUnique(id, dictionaryType, value),
                DICTIONARY_DATA_VALUE_DUPLICATE);
    }

    @Test
    public void getDictionaryDataCountByDictionaryType() {
        // mock data
        dictionaryDataMapper.insert(randomDictionaryDataEntity(o -> o.setDictType("focela_sample")));
        dictionaryDataMapper.insert(randomDictionaryDataEntity(o -> o.setDictType("focela_alternate")));
        dictionaryDataMapper.insert(randomDictionaryDataEntity(o -> o.setDictType("focela_sample")));
        // prepare parameters
        String dictionaryType = "focela_sample";

        // invoke
        long count = dictionaryDataService.getDictionaryDataCountByDictionaryType(dictionaryType);
        // verify
        assertEquals(2L, count);
    }

    @Test
    public void validateDictionaryDataList_success() {
        // mock data
        DictionaryDataEntity dictionaryDataEntity = randomDictionaryDataEntity().setStatus(CommonStatusEnum.ENABLE.getStatus());
        dictionaryDataMapper.insert(dictionaryDataEntity);
        // prepare parameters
        String dictionaryType = dictionaryDataEntity.getDictType();
        List<String> values = singletonList(dictionaryDataEntity.getValue());

        // invoke, no assertion needed
        dictionaryDataService.validateDictionaryDataList(dictionaryType, values);
    }

    @Test
    public void validateDictionaryDataList_missing() {
        // prepare parameters
        String dictionaryType = randomString();
        List<String> values = singletonList(randomString());

        // invoke and assert exception
        assertServiceException(() -> dictionaryDataService.validateDictionaryDataList(dictionaryType, values), DICTIONARY_DATA_NOT_FOUND);
    }

    @Test
    public void validateDictionaryDataList_disabled() {
        // mock data
        DictionaryDataEntity dictionaryDataEntity = randomDictionaryDataEntity().setStatus(CommonStatusEnum.DISABLE.getStatus());
        dictionaryDataMapper.insert(dictionaryDataEntity);
        // prepare parameters
        String dictionaryType = dictionaryDataEntity.getDictType();
        List<String> values = singletonList(dictionaryDataEntity.getValue());

        // invoke and assert exception
        assertServiceException(() -> dictionaryDataService.validateDictionaryDataList(dictionaryType, values),
                DICTIONARY_DATA_NOT_ENABLED, dictionaryDataEntity.getLabel());
    }

    @Test
    public void getDictionaryData_dictType() {
        // mock data
        DictionaryDataEntity dictionaryDataEntity = randomDictionaryDataEntity().setDictType("focela_sample").setValue("1");
        dictionaryDataMapper.insert(dictionaryDataEntity);
        DictionaryDataEntity dictionaryDataEntity02 = randomDictionaryDataEntity().setDictType("focela_sample").setValue("2");
        dictionaryDataMapper.insert(dictionaryDataEntity02);
        // prepare parameters
        String dictionaryType = "focela_sample";
        String value = "1";

        // invoke
        DictionaryDataEntity dbDictionaryData = dictionaryDataService.getDictionaryData(dictionaryType, value);
        // assert
        assertEquals(dictionaryDataEntity, dbDictionaryData);
    }

    @Test
    public void parseDictionaryData() {
        // mock data
        DictionaryDataEntity dictionaryDataEntity = randomDictionaryDataEntity().setDictType("focela_sample").setLabel("1");
        dictionaryDataMapper.insert(dictionaryDataEntity);
        DictionaryDataEntity dictionaryDataEntity02 = randomDictionaryDataEntity().setDictType("focela_sample").setLabel("2");
        dictionaryDataMapper.insert(dictionaryDataEntity02);
        // prepare parameters
        String dictionaryType = "focela_sample";
        String label = "1";

        // invoke
        DictionaryDataEntity dbDictionaryData = dictionaryDataService.parseDictionaryData(dictionaryType, label);
        // assert
        assertEquals(dictionaryDataEntity, dbDictionaryData);
    }

    // ========== random object ==========

    @SafeVarargs
    private static DictionaryDataEntity randomDictionaryDataEntity(Consumer<DictionaryDataEntity>... consumers) {
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
    private static DictionaryTypeEntity randomDictionaryTypeEntity(String type) {
        return randomPojo(DictionaryTypeEntity.class, o -> {
            o.setType(type);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // ensure status is enabled
        });
    }

}
