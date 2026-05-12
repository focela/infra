package com.focela.platform.framework.common.business.system.dictionary;

import com.focela.platform.framework.common.business.system.dictionary.dto.DictionaryDataRespDTO;

import java.util.List;

/**
 * 字典数据 API 接口
 *
 * @author 芋道源码
 */
public interface DictionaryDataCommonApi {

    /**
     * 获得指定字典类型的字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictionaryDataRespDTO> getDictDataList(String dictType);

}
