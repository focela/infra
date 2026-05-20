package com.focela.platform.system.service.dictionary;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.dictionary.request.type.DictionaryTypePageRequest;
import com.focela.platform.system.controller.admin.dictionary.request.type.DictionaryTypeSaveRequest;
import com.focela.platform.system.domain.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.system.repository.mapper.dictionary.DictionaryTypeMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(DefaultDictionaryTypeService.class)
public class DefaultDictionaryTypeServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultDictionaryTypeService dictionaryTypeService;

    @Resource
    private DictionaryTypeMapper dictionaryTypeMapper;
    @MockitoBean
    private DictionaryDataService dictionaryDataService;

    @Test
    public void testGetDictTypePage() {
       // mock data
       DictionaryTypeEntity dbDictionaryType = randomPojo(DictionaryTypeEntity.class, o -> { // will be queried later
           o.setName("yunai");
           o.setType("Focela");
           o.setStatus(CommonStatusEnum.ENABLE.getStatus());
           o.setCreateTime(buildTime(2021, 1, 15));
       });
       dictionaryTypeMapper.insert(dbDictionaryType);
       // test name mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setName("tudou")));
       // test type mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setType("Potato")));
       // test status mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
       // test createTime mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setCreateTime(buildTime(2021, 1, 1))));
       // prepare parameters
       DictionaryTypePageRequest request = new DictionaryTypePageRequest();
       request.setName("nai");
       request.setType("Focela");
       request.setStatus(CommonStatusEnum.ENABLE.getStatus());
       request.setCreateTime(buildBetweenTime(2021, 1, 10, 2021, 1, 20));

       // invoke
       PageResult<DictionaryTypeEntity> pageResult = dictionaryTypeService.getDictTypePage(request);
       // assert
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbDictionaryType, pageResult.getList().get(0));
    }

    @Test
    public void testGetDictType_id() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);
        // prepare parameters
        Long id = dbDictionaryType.getId();

        // invoke
        DictionaryTypeEntity dictionaryType = dictionaryTypeService.getDictType(id);
        // assert
        assertNotNull(dictionaryType);
        assertPojoEquals(dbDictionaryType, dictionaryType);
    }

    @Test
    public void testGetDictType_type() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);
        // prepare parameters
        String type = dbDictionaryType.getType();

        // invoke
        DictionaryTypeEntity dictionaryType = dictionaryTypeService.getDictType(type);
        // assert
        assertNotNull(dictionaryType);
        assertPojoEquals(dbDictionaryType, dictionaryType);
    }

    @Test
    public void testCreateDictType_success() {
        // prepare parameters
        DictionaryTypeSaveRequest request = randomPojo(DictionaryTypeSaveRequest.class,
                o -> o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()))
                .setId(null); // avoid id being assigned

        // invoke
        Long dictionaryTypeId = dictionaryTypeService.createDictType(request);
        // assert
        assertNotNull(dictionaryTypeId);
        // verify record properties are correct
        DictionaryTypeEntity dictionaryType = dictionaryTypeMapper.selectById(dictionaryTypeId);
        assertPojoEquals(request, dictionaryType, "id");
    }

    @Test
    public void testUpdateDictType_success() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record
        // prepare parameters
        DictionaryTypeSaveRequest request = randomPojo(DictionaryTypeSaveRequest.class, o -> {
            o.setId(dbDictionaryType.getId()); // set updated ID
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus());
        });

        // invoke
        dictionaryTypeService.updateDictType(request);
        // verify update is correct
        DictionaryTypeEntity dictionaryType = dictionaryTypeMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, dictionaryType);
    }

    @Test
    public void testDeleteDictType_success() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictionaryType.getId();

        // invoke
        dictionaryTypeService.deleteDictType(id);
        // verify data no longer exists
        assertNull(dictionaryTypeMapper.selectById(id));
    }

    @Test
    public void testDeleteDictType_hasChildren() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictionaryType.getId();
        // mock the method
        when(dictionaryDataService.getDictDataCountByDictType(eq(dbDictionaryType.getType()))).thenReturn(1L);

        // invoke and assert exception
        assertServiceException(() -> dictionaryTypeService.deleteDictType(id), DICT_TYPE_HAS_CHILDREN);
    }

    @Test
    public void testGetDictTypeList() {
        // prepare parameters
        DictionaryTypeEntity dictTypeEntity01 = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dictTypeEntity01);
        DictionaryTypeEntity dictTypeEntity02 = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dictTypeEntity02);
        // mock the method

        // invoke
        List<DictionaryTypeEntity> dictionaryTypeEntities = dictionaryTypeService.getDictTypeList();
        // assert
        assertEquals(2, dictionaryTypeEntities.size());
        assertPojoEquals(dictTypeEntity01, dictionaryTypeEntities.get(0));
        assertPojoEquals(dictTypeEntity02, dictionaryTypeEntities.get(1));
    }

    @Test
    public void testValidateDictDataExists_success() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record

        // invoke succeeded
        dictionaryTypeService.validateDictTypeExists(dbDictionaryType.getId());
    }

    @Test
    public void testValidateDictDataExists_notExists() {
        assertServiceException(() -> dictionaryTypeService.validateDictTypeExists(randomLongId()), DICT_TYPE_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeUnique_success() {
        // invoke, succeeded
        dictionaryTypeService.validateDictTypeUnique(randomLongId(), randomString());
    }

    @Test
    public void testValidateDictTypeUnique_valueDuplicateForCreate() {
        // prepare parameters
        String type = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictTypeEntity(o -> o.setType(type)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeUnique(null, type),
                DICT_TYPE_TYPE_DUPLICATE);
    }

    @Test
    public void testValidateDictTypeUnique_valueDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String type = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictTypeEntity(o -> o.setType(type)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeUnique(id, type),
                DICT_TYPE_TYPE_DUPLICATE);
    }

    @Test
    public void testValidateDictionaryTypeNameUnique_success() {
        // invoke, succeeded
        dictionaryTypeService.validateDictTypeNameUnique(randomLongId(), randomString());
    }

    @Test
    public void testValidateDictTypeNameUnique_nameDuplicateForCreate() {
        // prepare parameters
        String name = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictTypeEntity(o -> o.setName(name)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeNameUnique(null, name),
                DICT_TYPE_NAME_DUPLICATE);
    }

    @Test
    public void testValidateDictTypeNameUnique_nameDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String name = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictTypeEntity(o -> o.setName(name)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeNameUnique(id, name),
                DICT_TYPE_NAME_DUPLICATE);
    }

    // ========== random object ==========

    @SafeVarargs
    private static DictionaryTypeEntity randomDictTypeEntity(Consumer<DictionaryTypeEntity>... consumers) {
        Consumer<DictionaryTypeEntity> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
        };
        return randomPojo(DictionaryTypeEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
