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
    @SuppressWarnings("deprecation")
    default Long createDictionaryData(DictionaryDataSaveRequest createRequest) {
        return createDictData(createRequest);
    }

    /**
     * Create dictionary data
     *
     * @param createRequest dictionary data info
     * @return dictionary data ID
     * @deprecated use {@link #createDictionaryData(DictionaryDataSaveRequest)}
     */
    @Deprecated
    Long createDictData(DictionaryDataSaveRequest createRequest);

    /**
     * Update dictionary data
     *
     * @param updateRequest dictionary data info
     */
    @SuppressWarnings("deprecation")
    default void updateDictionaryData(DictionaryDataSaveRequest updateRequest) {
        updateDictData(updateRequest);
    }

    /**
     * Update dictionary data
     *
     * @param updateRequest dictionary data info
     * @deprecated use {@link #updateDictionaryData(DictionaryDataSaveRequest)}
     */
    @Deprecated
    void updateDictData(DictionaryDataSaveRequest updateRequest);

    /**
     * Delete dictionary data
     *
     * @param id dictionary data ID
     */
    @SuppressWarnings("deprecation")
    default void deleteDictionaryData(Long id) {
        deleteDictData(id);
    }

    /**
     * Delete dictionary data
     *
     * @param id dictionary data ID
     * @deprecated use {@link #deleteDictionaryData(Long)}
     */
    @Deprecated
    void deleteDictData(Long id);

    /**
     * Batch delete dictionary data
     *
     * @param ids dictionary data ID list
     */
    @SuppressWarnings("deprecation")
    default void deleteDictionaryDataList(List<Long> ids) {
        deleteDictDataList(ids);
    }

    /**
     * Batch delete dictionary data
     *
     * @param ids dictionary data ID list
     * @deprecated use {@link #deleteDictionaryDataList(List)}
     */
    @Deprecated
    void deleteDictDataList(List<Long> ids);

    /**
     * Get the dictionary data list
     *
     * @param status   status
     * @param dictionaryType dictionary type
     * @return full dictionary data list
     */
    @SuppressWarnings("deprecation")
    default List<DictionaryDataEntity> getDictionaryDataList(@Nullable Integer status, @Nullable String dictionaryType) {
        return getDictDataList(status, dictionaryType);
    }

    /**
     * Get the dictionary data list
     *
     * @param status   status
     * @param dictionaryType dictionary type
     * @return full dictionary data list
     * @deprecated use {@link #getDictionaryDataList(Integer, String)}
     */
    @Deprecated
    List<DictionaryDataEntity> getDictDataList(@Nullable Integer status, @Nullable String dictionaryType);

    /**
     * Get paginated dictionary data list
     *
     * @param pageRequest pagination request
     * @return paginated dictionary data list
     */
    @SuppressWarnings("deprecation")
    default PageResult<DictionaryDataEntity> getDictionaryDataPage(DictionaryDataPageRequest pageRequest) {
        return getDictDataPage(pageRequest);
    }

    /**
     * Get paginated dictionary data list
     *
     * @param pageRequest pagination request
     * @return paginated dictionary data list
     * @deprecated use {@link #getDictionaryDataPage(DictionaryDataPageRequest)}
     */
    @Deprecated
    PageResult<DictionaryDataEntity> getDictDataPage(DictionaryDataPageRequest pageRequest);

    /**
     * Get dictionary data details
     *
     * @param id dictionary data ID
     * @return dictionary data
     */
    @SuppressWarnings("deprecation")
    default DictionaryDataEntity getDictionaryData(Long id) {
        return getDictData(id);
    }

    /**
     * Get dictionary data details
     *
     * @param id dictionary data ID
     * @return dictionary data
     * @deprecated use {@link #getDictionaryData(Long)}
     */
    @Deprecated
    DictionaryDataEntity getDictData(Long id);

    /**
     * Get the count of data for the specified dictionary type
     *
     * @param dictionaryType dictionary type
     * @return data count
     */
    @SuppressWarnings("deprecation")
    default long getDictionaryDataCountByDictionaryType(String dictionaryType) {
        return getDictDataCountByDictType(dictionaryType);
    }

    /**
     * Get the count of data for the specified dictionary type
     *
     * @param dictionaryType dictionary type
     * @return data count
     * @deprecated use {@link #getDictionaryDataCountByDictionaryType(String)}
     */
    @Deprecated
    long getDictDataCountByDictType(String dictionaryType);

    /**
     * Validate whether the dictionary data entries are valid. The following cases are considered invalid:
     * 1. The dictionary data does not exist
     * 2. The dictionary data is disabled
     *
     * @param dictionaryType dictionary type
     * @param values   array of dictionary data values
     */
    @SuppressWarnings("deprecation")
    default void validateDictionaryDataList(String dictionaryType, Collection<String> values) {
        validateDictDataList(dictionaryType, values);
    }

    /**
     * Validate whether the dictionary data entries are valid. The following cases are considered invalid:
     * 1. The dictionary data does not exist
     * 2. The dictionary data is disabled
     *
     * @param dictionaryType dictionary type
     * @param values   array of dictionary data values
     * @deprecated use {@link #validateDictionaryDataList(String, Collection)}
     */
    @Deprecated
    void validateDictDataList(String dictionaryType, Collection<String> values);

    /**
     * Get the specified dictionary data
     *
     * @param dictionaryType dictionary type
     * @param value    dictionary data value
     * @return dictionary data
     */
    @SuppressWarnings("deprecation")
    default DictionaryDataEntity getDictionaryData(String dictionaryType, String value) {
        return getDictData(dictionaryType, value);
    }

    /**
     * Get the specified dictionary data
     *
     * @param dictionaryType dictionary type
     * @param value    dictionary data value
     * @return dictionary data
     * @deprecated use {@link #getDictionaryData(String, String)}
     */
    @Deprecated
    DictionaryDataEntity getDictData(String dictionaryType, String value);

    /**
     * Parse and get the specified dictionary data, from cache
     *
     * @param dictionaryType dictionary type
     * @param label    dictionary data label
     * @return dictionary data
     */
    @SuppressWarnings("deprecation")
    default DictionaryDataEntity parseDictionaryData(String dictionaryType, String label) {
        return parseDictData(dictionaryType, label);
    }

    /**
     * Parse and get the specified dictionary data, from cache
     *
     * @param dictionaryType dictionary type
     * @param label    dictionary data label
     * @return dictionary data
     * @deprecated use {@link #parseDictionaryData(String, String)}
     */
    @Deprecated
    DictionaryDataEntity parseDictData(String dictionaryType, String label);

    /**
     * Get the dictionary data list for the specified data type
     *
     * @param dictionaryType dictionary type
     * @return dictionary data list
     */
    @SuppressWarnings("deprecation")
    default List<DictionaryDataEntity> getDictionaryDataListByDictionaryType(String dictionaryType) {
        return getDictDataListByDictType(dictionaryType);
    }

    /**
     * Get the dictionary data list for the specified data type
     *
     * @param dictionaryType dictionary type
     * @return dictionary data list
     * @deprecated use {@link #getDictionaryDataListByDictionaryType(String)}
     */
    @Deprecated
    List<DictionaryDataEntity> getDictDataListByDictType(String dictionaryType);

}
