package com.focela.platform.system.api.dictionary;

import com.focela.platform.common.api.system.dictionary.DictionaryDataContractApi;

import java.util.Collection;

/**
 * Dictionary data API interface
 */
public interface DictionaryDataApi extends DictionaryDataContractApi {

    /**
     * Validate whether the dictionary data are valid. The following cases are considered invalid:
     * 1. dictionary data does not exist
     * 2. dictionary data is disabled
     *
     * @param dictionaryType dictionary type
     * @param values   dictionary data values
     */
    @SuppressWarnings("deprecation")
    default void validateDictionaryDataList(String dictionaryType, Collection<String> values) {
        validateDictDataList(dictionaryType, values);
    }

    /**
     * Validate whether the dictionary data are valid. The following cases are considered invalid:
     * 1. dictionary data does not exist
     * 2. dictionary data is disabled
     *
     * @param dictionaryType dictionary type
     * @param values   dictionary data values
     * @deprecated use {@link #validateDictionaryDataList(String, Collection)}
     */
    @Deprecated
    void validateDictDataList(String dictionaryType, Collection<String> values);

}
