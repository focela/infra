package com.focela.platform.dictionary.core;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.api.system.dictionary.DictionaryDataContractApi;
import com.focela.platform.common.utils.cache.CacheUtils;
import com.focela.platform.common.api.system.dictionary.dto.DictionaryDataRpcResponse;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;

/**
 * Dictionary utility class.
 */
@Slf4j
public class DictionaryFrameworkUtils {

    private static DictionaryDataContractApi dictionaryDataApi;

    /**
     * Cache of dictionary data keyed by dictionaryType.
     */
    private static final LoadingCache<String, List<DictionaryDataRpcResponse>> DICTIONARY_DATA_CACHE = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // Expiration: 1 minute
            new CacheLoader<String, List<DictionaryDataRpcResponse>>() {

                @Override
                public List<DictionaryDataRpcResponse> load(String dictionaryType) {
                    return dictionaryDataApi.getDictionaryDataList(dictionaryType);
                }

            });

    public static void init(DictionaryDataContractApi dictionaryDataApi) {
        DictionaryFrameworkUtils.dictionaryDataApi = dictionaryDataApi;
        log.info("[init][init DictionaryFrameworkUtils success]");
    }

    public static void clearCache() {
        DICTIONARY_DATA_CACHE.invalidateAll();
    }

    @SneakyThrows
    public static String parseDictDataLabel(String dictionaryType, Integer value) {
        if (value == null) {
            return null;
        }
        return parseDictDataLabel(dictionaryType, String.valueOf(value));
    }

    @SneakyThrows
    public static String parseDictDataLabel(String dictionaryType, String value) {
        List<DictionaryDataRpcResponse> dictionaryDataList = DICTIONARY_DATA_CACHE.get(dictionaryType);
        DictionaryDataRpcResponse dictionaryData = CollUtil.findOne(dictionaryDataList, data -> Objects.equals(data.getValue(), value));
        return dictionaryData != null ? dictionaryData.getLabel(): null;
    }

    @SneakyThrows
    public static List<String> getDictDataLabelList(String dictionaryType) {
        List<DictionaryDataRpcResponse> dictionaryDataList = DICTIONARY_DATA_CACHE.get(dictionaryType);
        return convertList(dictionaryDataList, DictionaryDataRpcResponse::getLabel);
    }

    @SneakyThrows
    public static String parseDictDataValue(String dictionaryType, String label) {
        List<DictionaryDataRpcResponse> dictionaryDataList = DICTIONARY_DATA_CACHE.get(dictionaryType);
        DictionaryDataRpcResponse dictionaryData = CollUtil.findOne(dictionaryDataList, data -> Objects.equals(data.getLabel(), label));
        return dictionaryData!= null ? dictionaryData.getValue(): null;
    }

    @SneakyThrows
    public static List<String> getDictDataValueList(String dictionaryType) {
        List<DictionaryDataRpcResponse> dictionaryDataList = DICTIONARY_DATA_CACHE.get(dictionaryType);
        return convertList(dictionaryDataList, DictionaryDataRpcResponse::getValue);
    }
}
