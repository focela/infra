package com.focela.platform.system.config.operatelog;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.ip.core.utils.AreaUtils;
import com.mzt.logapi.service.IParseFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link IParseFunction} implementation for area name
 */
@Slf4j
@Component
public class AreaParseFunction implements IParseFunction {

    public static final String NAME = "getArea";

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
        return AreaUtils.format(Convert.toInt(value));
    }

}
