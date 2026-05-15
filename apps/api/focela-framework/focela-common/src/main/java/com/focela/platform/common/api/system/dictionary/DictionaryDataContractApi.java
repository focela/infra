package com.focela.platform.common.api.system.dictionary;

import com.focela.platform.common.api.system.dictionary.dto.DictionaryDataRpcResponse;

import java.util.List;

/**
 * Dictionary data API interface.
 */
public interface DictionaryDataContractApi {

    /**
     * Get the dictionary data list for the specified dictionary type.
     *
     * @param dictType dictionary type
     * @return dictionary data list
     */
    List<DictionaryDataRpcResponse> getDictDataList(String dictType);

}
