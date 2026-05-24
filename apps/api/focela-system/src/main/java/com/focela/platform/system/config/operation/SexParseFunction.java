package com.focela.platform.system.config.operation;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.dictionary.core.DictionaryFrameworkUtils;
import com.focela.platform.system.constants.SystemDictionaryTypeConstants;
import com.mzt.logapi.service.IParseFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link IParseFunction} implementation for sex
 */
@Component
@Slf4j
public class SexParseFunction implements IParseFunction {

    public static final String NAME = "getSex";

    @Override
    public boolean executeBefore() {
        return true; // convert the value first, then compare
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
        return DictionaryFrameworkUtils.parseDictDataLabel(SystemDictionaryTypeConstants.USER_SEX, value.toString());
    }

}
