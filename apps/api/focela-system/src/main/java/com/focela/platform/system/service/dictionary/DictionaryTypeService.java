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
    Long createDictType(DictionaryTypeSaveRequest createRequest);

    /**
     * Update a dictionary type
     *
     * @param updateRequest dictionary type info
     */
    void updateDictType(DictionaryTypeSaveRequest updateRequest);

    /**
     * Delete a dictionary type
     *
     * @param id dictionary type ID
     */
    void deleteDictType(Long id);

    /**
     * Batch delete dictionary types
     *
     * @param ids dictionary type ID list
     */
    void deleteDictTypeList(List<Long> ids);

    /**
     * Get paginated list of dictionary types
     *
     * @param pageRequest pagination request
     * @return paginated dictionary type list
     */
    PageResult<DictionaryTypeEntity> getDictTypePage(DictionaryTypePageRequest pageRequest);

    /**
     * Get dictionary type details
     *
     * @param id dictionary type ID
     * @return dictionary type
     */
    DictionaryTypeEntity getDictType(Long id);

    /**
     * Get dictionary type details
     *
     * @param type dictionary type
     * @return dictionary type details
     */
    DictionaryTypeEntity getDictType(String type);

    /**
     * Get the full list of dictionary types
     *
     * @return dictionary type list
     */
    List<DictionaryTypeEntity> getDictTypeList();

}
