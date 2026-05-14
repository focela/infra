package com.focela.platform.framework.dictionary.core;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.contract.system.dictionary.DictionaryDataContractApi;
import com.focela.platform.framework.common.utils.cache.CacheUtils;
import com.focela.platform.framework.common.contract.system.dictionary.dto.DictionaryDataRpcResponse;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertList;

/**
 * Dictionary utility class.
 */
@Slf4j
public class DictionaryFrameworkUtils {

    private static DictionaryDataContractApi dictDataApi;

    /**
     * Cache of dictionary data keyed by dictType.
     */
    private static final LoadingCache<String, List<DictionaryDataRpcResponse>> GET_DICT_DATA_CACHE = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // Expiration: 1 minute
            new CacheLoader<String, List<DictionaryDataRpcResponse>>() {

                @Override
                public List<DictionaryDataRpcResponse> load(String dictType) {
                    return dictDataApi.getDictDataList(dictType);
                }

            });

    public static void init(DictionaryDataContractApi dictDataApi) {
        DictionaryFrameworkUtils.dictDataApi = dictDataApi;
        log.info("[init][init DictionaryFrameworkUtils success]");
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
        List<DictionaryDataRpcResponse> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        DictionaryDataRpcResponse dictData = CollUtil.findOne(dictDatas, data -> Objects.equals(data.getValue(), value));
        return dictData != null ? dictData.getLabel(): null;
    }

    @SneakyThrows
    public static List<String> getDictDataLabelList(String dictType) {
        List<DictionaryDataRpcResponse> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        return convertList(dictDatas, DictionaryDataRpcResponse::getLabel);
    }

    @SneakyThrows
    public static String parseDictDataValue(String dictType, String label) {
        List<DictionaryDataRpcResponse> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        DictionaryDataRpcResponse dictData = CollUtil.findOne(dictDatas, data -> Objects.equals(data.getLabel(), label));
        return dictData!= null ? dictData.getValue(): null;
    }

    @SneakyThrows
    public static List<String> getDictDataValueList(String dictType) {
        List<DictionaryDataRpcResponse> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        return convertList(dictDatas, DictionaryDataRpcResponse::getValue);
    }
}
