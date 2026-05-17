package com.focela.platform.system.service.dictionary;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.ArrayUtils;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypePageRequest;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypeSaveRequest;
import com.focela.platform.system.entity.dictionary.DictionaryTypeEntity;
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
    private DefaultDictionaryTypeService dictTypeService;

    @Resource
    private DictionaryTypeMapper dictTypeMapper;
    @MockitoBean
    private DictionaryDataService dictDataService;

    @Test
    public void testGetDictTypePage() {
       // mock data
       DictionaryTypeEntity dbDictType = randomPojo(DictionaryTypeEntity.class, o -> { // will be queried later
           o.setName("yunai");
           o.setType("Focela");
           o.setStatus(CommonStatusEnum.ENABLE.getStatus());
           o.setCreateTime(buildTime(2021, 1, 15));
       });
       dictTypeMapper.insert(dbDictType);
       // test name mismatch
       dictTypeMapper.insert(cloneIgnoreId(dbDictType, o -> o.setName("tudou")));
       // test type mismatch
       dictTypeMapper.insert(cloneIgnoreId(dbDictType, o -> o.setType("Potato")));
       // test status mismatch
       dictTypeMapper.insert(cloneIgnoreId(dbDictType, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
       // test createTime mismatch
       dictTypeMapper.insert(cloneIgnoreId(dbDictType, o -> o.setCreateTime(buildTime(2021, 1, 1))));
       // prepare parameters
       DictionaryTypePageRequest request = new DictionaryTypePageRequest();
       request.setName("nai");
       request.setType("Focela");
       request.setStatus(CommonStatusEnum.ENABLE.getStatus());
       request.setCreateTime(buildBetweenTime(2021, 1, 10, 2021, 1, 20));

       // invoke
       PageResult<DictionaryTypeEntity> pageResult = dictTypeService.getDictTypePage(request);
       // assert
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbDictType, pageResult.getList().get(0));
    }

    @Test
    public void testGetDictType_id() {
        // mock data
        DictionaryTypeEntity dbDictType = randomDictTypeDO();
        dictTypeMapper.insert(dbDictType);
        // prepare parameters
        Long id = dbDictType.getId();

        // invoke
        DictionaryTypeEntity dictType = dictTypeService.getDictType(id);
        // assert
        assertNotNull(dictType);
        assertPojoEquals(dbDictType, dictType);
    }

    @Test
    public void testGetDictType_type() {
        // mock data
        DictionaryTypeEntity dbDictType = randomDictTypeDO();
        dictTypeMapper.insert(dbDictType);
        // prepare parameters
        String type = dbDictType.getType();

        // invoke
        DictionaryTypeEntity dictType = dictTypeService.getDictType(type);
        // assert
        assertNotNull(dictType);
        assertPojoEquals(dbDictType, dictType);
    }

    @Test
    public void testCreateDictType_success() {
        // prepare parameters
        DictionaryTypeSaveRequest request = randomPojo(DictionaryTypeSaveRequest.class,
                o -> o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()))
                .setId(null); // avoid id being assigned

        // invoke
        Long dictTypeId = dictTypeService.createDictType(request);
        // assert
        assertNotNull(dictTypeId);
        // verify record properties are correct
        DictionaryTypeEntity dictType = dictTypeMapper.selectById(dictTypeId);
        assertPojoEquals(request, dictType, "id");
    }

    @Test
    public void testUpdateDictType_success() {
        // mock data
        DictionaryTypeEntity dbDictType = randomDictTypeDO();
        dictTypeMapper.insert(dbDictType);// @Sql: first insert an existing record
        // prepare parameters
        DictionaryTypeSaveRequest request = randomPojo(DictionaryTypeSaveRequest.class, o -> {
            o.setId(dbDictType.getId()); // set updated ID
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus());
        });

        // invoke
        dictTypeService.updateDictType(request);
        // verify update is correct
        DictionaryTypeEntity dictType = dictTypeMapper.selectById(request.getId()); // get the latest
        assertPojoEquals(request, dictType);
    }

    @Test
    public void testDeleteDictType_success() {
        // mock data
        DictionaryTypeEntity dbDictType = randomDictTypeDO();
        dictTypeMapper.insert(dbDictType);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictType.getId();

        // invoke
        dictTypeService.deleteDictType(id);
        // verify data no longer exists
        assertNull(dictTypeMapper.selectById(id));
    }

    @Test
    public void testDeleteDictType_hasChildren() {
        // mock data
        DictionaryTypeEntity dbDictType = randomDictTypeDO();
        dictTypeMapper.insert(dbDictType);// @Sql: first insert an existing record
        // prepare parameters
        Long id = dbDictType.getId();
        // mock the method
        when(dictDataService.getDictDataCountByDictType(eq(dbDictType.getType()))).thenReturn(1L);

        // invoke and assert exception
        assertServiceException(() -> dictTypeService.deleteDictType(id), DICT_TYPE_HAS_CHILDREN);
    }

    @Test
    public void testGetDictTypeList() {
        // prepare parameters
        DictionaryTypeEntity dictTypeDO01 = randomDictTypeDO();
        dictTypeMapper.insert(dictTypeDO01);
        DictionaryTypeEntity dictTypeDO02 = randomDictTypeDO();
        dictTypeMapper.insert(dictTypeDO02);
        // mock the method

        // invoke
        List<DictionaryTypeEntity> dictTypeDOList = dictTypeService.getDictTypeList();
        // assert
        assertEquals(2, dictTypeDOList.size());
        assertPojoEquals(dictTypeDO01, dictTypeDOList.get(0));
        assertPojoEquals(dictTypeDO02, dictTypeDOList.get(1));
    }

    @Test
    public void testValidateDictDataExists_success() {
        // mock data
        DictionaryTypeEntity dbDictType = randomDictTypeDO();
        dictTypeMapper.insert(dbDictType);// @Sql: first insert an existing record

        // invoke succeeded
        dictTypeService.validateDictTypeExists(dbDictType.getId());
    }

    @Test
    public void testValidateDictDataExists_notExists() {
        assertServiceException(() -> dictTypeService.validateDictTypeExists(randomLongId()), DICT_TYPE_NOT_EXISTS);
    }

    @Test
    public void testValidateDictTypeUnique_success() {
        // invoke, succeeded
        dictTypeService.validateDictTypeUnique(randomLongId(), randomString());
    }

    @Test
    public void testValidateDictTypeUnique_valueDuplicateForCreate() {
        // prepare parameters
        String type = randomString();
        // mock data
        dictTypeMapper.insert(randomDictTypeDO(o -> o.setType(type)));

        // invoke, verify exception
        assertServiceException(() -> dictTypeService.validateDictTypeUnique(null, type),
                DICT_TYPE_TYPE_DUPLICATE);
    }

    @Test
    public void testValidateDictTypeUnique_valueDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String type = randomString();
        // mock data
        dictTypeMapper.insert(randomDictTypeDO(o -> o.setType(type)));

        // invoke, verify exception
        assertServiceException(() -> dictTypeService.validateDictTypeUnique(id, type),
                DICT_TYPE_TYPE_DUPLICATE);
    }

    @Test
    public void testValidateDictionaryTypeNameUnique_success() {
        // invoke, succeeded
        dictTypeService.validateDictTypeNameUnique(randomLongId(), randomString());
    }

    @Test
    public void testValidateDictTypeNameUnique_nameDuplicateForCreate() {
        // prepare parameters
        String name = randomString();
        // mock data
        dictTypeMapper.insert(randomDictTypeDO(o -> o.setName(name)));

        // invoke, verify exception
        assertServiceException(() -> dictTypeService.validateDictTypeNameUnique(null, name),
                DICT_TYPE_NAME_DUPLICATE);
    }

    @Test
    public void testValidateDictTypeNameUnique_nameDuplicateForUpdate() {
        // prepare parameters
        Long id = randomLongId();
        String name = randomString();
        // mock data
        dictTypeMapper.insert(randomDictTypeDO(o -> o.setName(name)));

        // invoke, verify exception
        assertServiceException(() -> dictTypeService.validateDictTypeNameUnique(id, name),
                DICT_TYPE_NAME_DUPLICATE);
    }

    // ========== random object ==========

    @SafeVarargs
    private static DictionaryTypeEntity randomDictTypeDO(Consumer<DictionaryTypeEntity>... consumers) {
        Consumer<DictionaryTypeEntity> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // ensure status range
        };
        return randomPojo(DictionaryTypeEntity.class, ArrayUtils.append(consumer, consumers));
    }

}
