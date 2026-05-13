package com.focela.platform.module.system.service.dictionary;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypePageRequest;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypeSaveRequest;
import com.focela.platform.module.system.entity.dictionary.DictionaryTypeEntity;

import java.util.List;

/**
 * 字典类型 Service 接口
 */
public interface DictionaryTypeService {

    /**
     * 创建字典类型
     *
     * @param createRequest 字典类型信息
     * @return 字典类型编号
     */
    Long createDictType(DictionaryTypeSaveRequest createRequest);

    /**
     * 更新字典类型
     *
     * @param updateRequest 字典类型信息
     */
    void updateDictType(DictionaryTypeSaveRequest updateRequest);

    /**
     * 删除字典类型
     *
     * @param id 字典类型编号
     */
    void deleteDictType(Long id);

    /**
     * 批量删除字典类型
     *
     * @param ids 字典类型编号列表
     */
    void deleteDictTypeList(List<Long> ids);

    /**
     * 获得字典类型分页列表
     *
     * @param pageRequest 分页请求
     * @return 字典类型分页列表
     */
    PageResult<DictionaryTypeEntity> getDictTypePage(DictionaryTypePageRequest pageRequest);

    /**
     * 获得字典类型详情
     *
     * @param id 字典类型编号
     * @return 字典类型
     */
    DictionaryTypeEntity getDictType(Long id);

    /**
     * 获得字典类型详情
     *
     * @param type 字典类型
     * @return 字典类型详情
     */
    DictionaryTypeEntity getDictType(String type);

    /**
     * 获得全部字典类型列表
     *
     * @return 字典类型列表
     */
    List<DictionaryTypeEntity> getDictTypeList();

}
