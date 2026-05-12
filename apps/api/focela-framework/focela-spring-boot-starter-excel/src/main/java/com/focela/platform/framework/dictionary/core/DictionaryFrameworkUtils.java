package com.focela.platform.framework.dictionary.core;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.business.system.dictionary.DictionaryDataCommonApi;
import com.focela.platform.framework.common.utils.cache.CacheUtils;
import com.focela.platform.framework.common.business.system.dictionary.dto.DictionaryDataRespDTO;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertList;

/**
 * 字典工具类
 *
 * @author 芋道源码
 */
@Slf4j
public class DictionaryFrameworkUtils {

    private static DictionaryDataCommonApi dictDataApi;

    /**
     * 针对 dictType 的字段数据缓存
     */
    private static final LoadingCache<String, List<DictionaryDataRespDTO>> GET_DICT_DATA_CACHE = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // 过期时间 1 分钟
            new CacheLoader<String, List<DictionaryDataRespDTO>>() {

                @Override
                public List<DictionaryDataRespDTO> load(String dictType) {
                    return dictDataApi.getDictDataList(dictType);
                }

            });

    public static void init(DictionaryDataCommonApi dictDataApi) {
        DictionaryFrameworkUtils.dictDataApi = dictDataApi;
        log.info("[init][初始化 DictionaryFrameworkUtils 成功]");
    }

    public static void clearCache() {
        GET_DICT_DATA_CACHE.invalidateAll();
    }

    @SneakyThrows
    public static String parseDictDataLabel(String dictType, Integer value) {
        if (value == null) {
            return null;
        }
        return parseDictDataLabel(dictType, String.valueOf(value));
    }

    @SneakyThrows
    public static String parseDictDataLabel(String dictType, String value) {
        List<DictionaryDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        DictionaryDataRespDTO dictData = CollUtil.findOne(dictDatas, data -> Objects.equals(data.getValue(), value));
        return dictData != null ? dictData.getLabel(): null;
    }

    @SneakyThrows
    public static List<String> getDictDataLabelList(String dictType) {
        List<DictionaryDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        return convertList(dictDatas, DictionaryDataRespDTO::getLabel);
    }

    @SneakyThrows
    public static String parseDictDataValue(String dictType, String label) {
        List<DictionaryDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        DictionaryDataRespDTO dictData = CollUtil.findOne(dictDatas, data -> Objects.equals(data.getLabel(), label));
        return dictData!= null ? dictData.getValue(): null;
    }

    @SneakyThrows
    public static List<String> getDictDataValueList(String dictType) {
        List<DictionaryDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        return convertList(dictDatas, DictionaryDataRespDTO::getValue);
    }
}
