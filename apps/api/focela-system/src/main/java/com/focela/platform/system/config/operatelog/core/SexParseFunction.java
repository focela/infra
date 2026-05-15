package com.focela.platform.system.config.operatelog.core;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.dictionary.core.DictionaryFrameworkUtils;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import com.mzt.logapi.service.IParseFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 行业的 {@link IParseFunction} 实现类
 */
@Component
@Slf4j
public class SexParseFunction implements IParseFunction {

    public static final String NAME = "getSex";

    @Override
    public boolean executeBefore() {
        return true; // 先转换值后对比
    }

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }
        return DictionaryFrameworkUtils.parseDictDataLabel(DictionaryTypeConstants.USER_SEX, value.toString());
    }

}
