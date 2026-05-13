package com.focela.platform.framework.dictionary.core.utils;

import cn.hutool.core.collection.ListUtil;
import com.focela.platform.framework.common.business.system.dictionary.DictionaryDataCommonApi;
import com.focela.platform.framework.common.business.system.dictionary.dto.DictionaryDataRpcResponse;
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
 * {@link DictionaryFrameworkUtils} 的单元测试
 */
public class DictionaryFrameworkUtilsTest extends BaseMockitoUnitTest {

    @Mock
    private DictionaryDataCommonApi dictDataApi;

    @BeforeEach
    public void setUp() {
        DictionaryFrameworkUtils.init(dictDataApi);
        DictionaryFrameworkUtils.clearCache();
    }

    @Test
    public void testParseDictDataLabel() {
        // mock 数据
        List<DictionaryDataRpcResponse> dictDatas = ListUtil.of(
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("cat").setLabel("猫")),
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("dog").setLabel("狗"))
        );
        // mock 方法
        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(dictDatas);

        // 断言返回值
        assertEquals("狗", DictionaryFrameworkUtils.parseDictDataLabel("animal", "dog"));
    }

    @Test
    public void testParseDictDataValue() {
        // mock 数据
        List<DictionaryDataRpcResponse> dictDatas = ListUtil.of(
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("cat").setLabel("猫")),
                randomPojo(DictionaryDataRpcResponse.class, o -> o.setDictType("animal").setValue("dog").setLabel("狗"))
        );
        // mock 方法
        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(dictDatas);

        // 断言返回值
        assertEquals("dog", DictionaryFrameworkUtils.parseDictDataValue("animal", "狗"));
    }

}
