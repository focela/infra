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
    public void getDictionaryTypePage() {
       // mock data
       DictionaryTypeEntity dbDictionaryType = randomPojo(DictionaryTypeEntity.class, o -> { // will be queried later
           o.setName("focela_sample");
           o.setType("Focela");
           o.setStatus(CommonStatusEnum.ENABLE.getStatus());
           o.setCreateTime(buildTime(2021, 1, 15));
       });
       dictionaryTypeMapper.insert(dbDictionaryType);
       // test name mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setName("focela_alternate")));
       // test type mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setType("Potato")));
       // test status mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
       // test createTime mismatch
       dictionaryTypeMapper.insert(cloneIgnoreId(dbDictionaryType, o -> o.setCreateTime(buildTime(2021, 1, 1))));
       // prepare parameters
       DictionaryTypePageRequest request = new DictionaryTypePageRequest();
       request.setName("focela_sample");
       request.setType("Focela");
       request.setStatus(CommonStatusEnum.ENABLE.getStatus());
       request.setCreateTime(buildBetweenTime(2021, 1, 10, 2021, 1, 20));

       // invoke
       PageResult<DictionaryTypeEntity> pageResult = dictionaryTypeService.getDictionaryTypePage(request);
       // assert
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbDictionaryType, pageResult.getList().get(0));
    }

    @Test
    public void getDictionaryTypeById() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);
        // prepare parameters
        Long id = dbDictionaryType.getId();

        // invoke
        DictionaryTypeEntity dictionaryType = dictionaryTypeService.getDictionaryType(id);
        // assert
        assertNotNull(dictionaryType);
        assertPojoEquals(dbDictionaryType, dictionaryType);
    }

    @Test
    public void getDictionaryTypeByType() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);
        // prepare parameters
        String type = dbDictionaryType.getType();

        // invoke
        DictionaryTypeEntity dictionaryType = dictionaryTypeService.getDictionaryType(type);
        // assert
        assertNotNull(dictionaryType);
        assertPojoEquals(dbDictionaryType, dictionaryType);
    }

    @Test
    public void createDictionaryType_success() {
        // prepare parameters
        DictionaryTypeSaveRequest request = randomPojo(DictionaryTypeSaveRequest.class,
                o -> o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()))
                .setId(null); // avoid id being assigned

        // invoke
        Long dictionaryTypeId = dictionaryTypeService.createDictionaryType(request);
        // assert
        assertNotNull(dictionaryTypeId);
        // verify record properties are correct
        DictionaryTypeEntity dictionaryType = dictionaryTypeMapper.selectById(dictionaryTypeId);
        assertPojoEquals(request, dictionaryType, "id");
    }

    @Test
    public void updateDictionaryType_success() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record
        // prepare parameters
        DictionaryTypeSaveRequest request = randomPojo(DictionaryTypeSaveRequest.class, o -> {
            o.setId(dbDictionaryType.getId()); // set updated ID
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus());
        });

        // invoke
        dictionaryTypeService.updateDictionaryType(request);
        // verify update is correct
        DictionaryTypeEntity dictionaryType = dictionaryTypeMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, dictionaryType);
    }

    @Test
    public void deleteDictionaryType_success() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictionaryType.getId();

        // invoke
        dictionaryTypeService.deleteDictionaryType(id);
        // verify data no longer exists
        assertNull(dictionaryTypeMapper.selectById(id));
    }

    @Test
    public void deleteDictionaryType_hasChildren() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictionaryType.getId();
        // mock the method
        when(dictionaryDataService.getDictionaryDataCountByDictionaryType(eq(dbDictionaryType.getType()))).thenReturn(1L);

        // invoke and assert exception
        assertServiceException(() -> dictionaryTypeService.deleteDictionaryType(id), DICTIONARY_TYPE_HAS_CHILDREN);
    }

    @Test
    public void getDictionaryTypeList() {
        // prepare parameters
        DictionaryTypeEntity dictionaryTypeEntity01 = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dictionaryTypeEntity01);
        DictionaryTypeEntity dictionaryTypeEntity02 = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dictionaryTypeEntity02);
        // mock the method

        // invoke
        List<DictionaryTypeEntity> dictionaryTypeEntities = dictionaryTypeService.getDictionaryTypeList();
        // assert
        assertEquals(2, dictionaryTypeEntities.size());
        assertPojoEquals(dictionaryTypeEntity01, dictionaryTypeEntities.get(0));
        assertPojoEquals(dictionaryTypeEntity02, dictionaryTypeEntities.get(1));
    }

    @Test
    public void validateDictionaryDataExists_success() {
        // mock data
        DictionaryTypeEntity dbDictionaryType = randomDictionaryTypeEntity();
        dictionaryTypeMapper.insert(dbDictionaryType);// @Sql: first insert an existing record

        // invoke succeeded
        dictionaryTypeService.validateDictTypeExists(dbDictionaryType.getId());
    }

    @Test
    public void validateDictionaryDataExists_missing() {
        assertServiceException(() -> dictionaryTypeService.validateDictTypeExists(randomLongId()), DICTIONARY_TYPE_NOT_FOUND);
    }

    @Test
    public void validateDictionaryTypeUnique_success() {
        // invoke, succeeded
        dictionaryTypeService.validateDictTypeUnique(randomLongId(), randomString());
    }

    @Test
    public void validateDictionaryTypeUnique_valueDuplicateForCreate() {
        // prepare parameters
        String type = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictionaryTypeEntity(o -> o.setType(type)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeUnique(null, type),
                DICTIONARY_TYPE_TYPE_DUPLICATE);
    }

    @Test
    public void validateDictionaryTypeUnique_valueDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String type = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictionaryTypeEntity(o -> o.setType(type)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeUnique(id, type),
                DICTIONARY_TYPE_TYPE_DUPLICATE);
    }

    @Test
    public void validateDictionaryTypeNameUnique_success() {
        // invoke, succeeded
        dictionaryTypeService.validateDictTypeNameUnique(randomLongId(), randomString());
    }

    @Test
    public void validateDictionaryTypeNameUnique_nameDuplicateForCreate() {
        // prepare parameters
        String name = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictionaryTypeEntity(o -> o.setName(name)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeNameUnique(null, name),
                DICTIONARY_TYPE_NAME_DUPLICATE);
    }

    @Test
    public void validateDictionaryTypeNameUnique_nameDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String name = randomString();
        // mock data
        dictionaryTypeMapper.insert(randomDictionaryTypeEntity(o -> o.setName(name)));

        // invoke, verify exception
        assertServiceException(() -> dictionaryTypeService.validateDictTypeNameUnique(id, name),
                DICTIONARY_TYPE_NAME_DUPLICATE);
    }

    // ========== random object ==========

    @SafeVarargs
    private static DictionaryTypeEntity randomDictionaryTypeEntity(Consumer<DictionaryTypeEntity>... consumers) {
        Consumer<DictionaryTypeEntity> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
        };
        return randomPojo(DictionaryTypeEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
