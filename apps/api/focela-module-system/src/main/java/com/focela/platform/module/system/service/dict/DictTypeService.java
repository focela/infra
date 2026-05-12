package com.focela.platform.module.system.service.dict;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.module.system.controller.admin.dict.dto.type.DictTypePageRequest;
import com.focela.platform.module.system.controller.admin.dict.dto.type.DictTypeSaveRequest;
import com.focela.platform.module.system.repository.entity.dict.DictTypeEntity;

import java.util.List;

/**
 * 字典类型 Service 接口
 *
 * @author 芋道源码
 */
public interface DictTypeService {

    /**
     * 创建字典类型
     *
     * @param createRequest 字典类型信息
     * @return 字典类型编号
     */
    Long createDictType(DictTypeSaveRequest createRequest);

    /**
     * 更新字典类型
     *
     * @param updateRequest 字典类型信息
     */
    void updateDictType(DictTypeSaveRequest updateRequest);

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
    PageResult<DictTypeEntity> getDictTypePage(DictTypePageRequest pageRequest);

    /**
     * 获得字典类型详情
     *
     * @param id 字典类型编号
     * @return 字典类型
     */
    DictTypeEntity getDictType(Long id);

    /**
     * 获得字典类型详情
     *
     * @param type 字典类型
     * @return 字典类型详情
     */
    DictTypeEntity getDictType(String type);

    /**
     * 获得全部字典类型列表
     *
     * @return 字典类型列表
     */
    List<DictTypeEntity> getDictTypeList();

}
