package com.focela.platform.framework.dictionary.core.utils;

import cn.hutool.core.collection.ListUtil;
import com.focela.platform.framework.common.api.system.dictionary.DictionaryDataContractApi;
import com.focela.platform.framework.common.api.system.dictionary.dto.DictionaryDataRpcResponse;
import com.focela.platform.framework.dictionary.core.DictionaryFrameworkUtils;
import com.focela.platform.framework.test.core.support.BaseMockitoUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static com.focela.platform.framework.test.core.utils.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link DictionaryFrameworkUtils}.
 */
public class DictionaryFrameworkUtilsTest extends BaseMockitoUnitTest {

    @Mock
    private DictionaryDataContractApi dictDataApi;

    @BeforeEach
    public void setUp() {
        DictionaryFrameworkUtils.init(dictDataApi);
        DictionaryFrameworkUtils.clearCache();
    }

    @Test
    public void testParseDictDataLabel() {
        // mock data
        List<DictionaryDataRpcResponse> dictDatas = ListUtil.of(
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("cat").setLabel("Cat")),
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("dog").setLabel("Dog"))
        );
        // mock the method
        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(dictDatas);

        // assert the return value
        assertEquals("Dog", DictionaryFrameworkUtils.parseDictDataLabel("animal", "dog"));
    }

    @Test
    public void testParseDictDataValue() {
        // mock data
        List<DictionaryDataRpcResponse> dictDatas = ListUtil.of(
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("cat").setLabel("Cat")),
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("dog").setLabel("Dog"))
        );
        // mock the method
        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(dictDatas);

        // assert the return value
        assertEquals("dog", DictionaryFrameworkUtils.parseDictDataValue("animal", "Dog"));
    }

}
