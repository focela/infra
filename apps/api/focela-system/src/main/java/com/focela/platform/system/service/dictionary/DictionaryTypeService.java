package com.focela.platform.system.service.dictionary;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.system.controller.admin.dictionary.request.type.DictionaryTypePageRequest;
import com.focela.platform.system.controller.admin.dictionary.request.type.DictionaryTypeSaveRequest;
import com.focela.platform.system.domain.entity.dictionary.DictionaryTypeEntity;

import java.util.List;

/**
 * Dictionary type Service interface
 */
public interface DictionaryTypeService {

    /**
     * Create a dictionary type
     *
     * @param createRequest dictionary type info
     * @return dictionary type ID
     */
    @SuppressWarnings("deprecation")
    default Long createDictionaryType(DictionaryTypeSaveRequest createRequest) {
        return createDictType(createRequest);
    }

    /**
     * Create a dictionary type
     *
     * @param createRequest dictionary type info
     * @return dictionary type ID
     * @deprecated use {@link #createDictionaryType(DictionaryTypeSaveRequest)}
     */
    @Deprecated
    Long createDictType(DictionaryTypeSaveRequest createRequest);

    /**
     * Update a dictionary type
     *
     * @param updateRequest dictionary type info
     */
    @SuppressWarnings("deprecation")
    default void updateDictionaryType(DictionaryTypeSaveRequest updateRequest) {
        updateDictType(updateRequest);
    }

    /**
     * Update a dictionary type
     *
     * @param updateRequest dictionary type info
     * @deprecated use {@link #updateDictionaryType(DictionaryTypeSaveRequest)}
     */
    @Deprecated
    void updateDictType(DictionaryTypeSaveRequest updateRequest);

    /**
     * Delete a dictionary type
     *
     * @param id dictionary type ID
     */
    @SuppressWarnings("deprecation")
    default void deleteDictionaryType(Long id) {
        deleteDictType(id);
    }

    /**
     * Delete a dictionary type
     *
     * @param id dictionary type ID
     * @deprecated use {@link #deleteDictionaryType(Long)}
     */
    @Deprecated
    void deleteDictType(Long id);

    /**
     * Batch delete dictionary types
     *
     * @param ids dictionary type ID list
     */
    @SuppressWarnings("deprecation")
    default void deleteDictionaryTypeList(List<Long> ids) {
        deleteDictTypeList(ids);
    }

    /**
     * Batch delete dictionary types
     *
     * @param ids dictionary type ID list
     * @deprecated use {@link #deleteDictionaryTypeList(List)}
     */
    @Deprecated
    void deleteDictTypeList(List<Long> ids);

    /**
     * Get paginated list of dictionary types
     *
     * @param pageRequest pagination request
     * @return paginated dictionary type list
     */
    @SuppressWarnings("deprecation")
    default PageResult<DictionaryTypeEntity> getDictionaryTypePage(DictionaryTypePageRequest pageRequest) {
        return getDictTypePage(pageRequest);
    }

    /**
     * Get paginated list of dictionary types
     *
     * @param pageRequest pagination request
     * @return paginated dictionary type list
     * @deprecated use {@link #getDictionaryTypePage(DictionaryTypePageRequest)}
     */
    @Deprecated
    PageResult<DictionaryTypeEntity> getDictTypePage(DictionaryTypePageRequest pageRequest);

    /**
     * Get dictionary type details
     *
     * @param id dictionary type ID
     * @return dictionary type
     */
    @SuppressWarnings("deprecation")
    default DictionaryTypeEntity getDictionaryType(Long id) {
        return getDictType(id);
    }

    /**
     * Get dictionary type details
     *
     * @param id dictionary type ID
     * @return dictionary type
     * @deprecated use {@link #getDictionaryType(Long)}
     */
    @Deprecated
    DictionaryTypeEntity getDictType(Long id);

    /**
     * Get dictionary type details
     *
     * @param type dictionary type
     * @return dictionary type details
     */
    @SuppressWarnings("deprecation")
    default DictionaryTypeEntity getDictionaryType(String type) {
        return getDictType(type);
    }

    /**
     * Get dictionary type details
     *
     * @param type dictionary type
     * @return dictionary type details
     * @deprecated use {@link #getDictionaryType(String)}
     */
    @Deprecated
    DictionaryTypeEntity getDictType(String type);

    /**
     * Get the full list of dictionary types
     *
     * @return dictionary type list
     */
    @SuppressWarnings("deprecation")
    default List<DictionaryTypeEntity> getDictionaryTypeList() {
        return getDictTypeList();
    }

    /**
     * Get the full list of dictionary types
     *
     * @return dictionary type list
     * @deprecated use {@link #getDictionaryTypeList()}
     */
    @Deprecated
    List<DictionaryTypeEntity> getDictTypeList();

}
