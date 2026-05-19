package com.focela.platform.system.service.dictionary;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataPageRequest;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataSaveRequest;
import com.focela.platform.system.domain.entity.dictionary.DictionaryDataEntity;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Dictionary data Service interface
 */
public interface DictionaryDataService {

    /**
     * Create dictionary data
     *
     * @param createRequest dictionary data info
     * @return dictionary data ID
     */
    Long createDictData(DictionaryDataSaveRequest createRequest);

    /**
     * Update dictionary data
     *
     * @param updateRequest dictionary data info
     */
    void updateDictData(DictionaryDataSaveRequest updateRequest);

    /**
     * Delete dictionary data
     *
     * @param id dictionary data ID
     */
    void deleteDictData(Long id);

    /**
     * Batch delete dictionary data
     *
     * @param ids dictionary data ID list
     */
    void deleteDictDataList(List<Long> ids);

    /**
     * Get the dictionary data list
     *
     * @param status   status
     * @param dictType dictionary type
     * @return full dictionary data list
     */
    List<DictionaryDataEntity> getDictDataList(@Nullable Integer status, @Nullable String dictType);

    /**
     * Get paginated dictionary data list
     *
     * @param pageRequest pagination request
     * @return paginated dictionary data list
     */
    PageResult<DictionaryDataEntity> getDictDataPage(DictionaryDataPageRequest pageRequest);

    /**
     * Get dictionary data details
     *
     * @param id dictionary data ID
     * @return dictionary data
     */
    DictionaryDataEntity getDictData(Long id);

    /**
     * Get the count of data for the specified dictionary type
     *
     * @param dictType dictionary type
     * @return data count
     */
    long getDictDataCountByDictType(String dictType);

    /**
     * Validate whether the dictionary data entries are valid. The following cases are considered invalid:
     * 1. The dictionary data does not exist
     * 2. The dictionary data is disabled
     *
     * @param dictType dictionary type
     * @param values   array of dictionary data values
     */
    void validateDictDataList(String dictType, Collection<String> values);

    /**
     * Get the specified dictionary data
     *
     * @param dictType dictionary type
     * @param value    dictionary data value
     * @return dictionary data
     */
    DictionaryDataEntity getDictData(String dictType, String value);

    /**
     * Parse and get the specified dictionary data, from cache
     *
     * @param dictType dictionary type
     * @param label    dictionary data label
     * @return dictionary data
     */
    DictionaryDataEntity parseDictData(String dictType, String label);

    /**
     * Get the dictionary data list for the specified data type
     *
     * @param dictType dictionary type
     * @return dictionary data list
     */
    List<DictionaryDataEntity> getDictDataListByDictType(String dictType);

}
