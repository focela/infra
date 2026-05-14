package com.focela.platform.framework.desensitize.core.slider.handler;

import com.focela.platform.framework.desensitize.core.slider.annotation.ChineseNameDesensitize;

/**
 * Desensitization handler for {@link ChineseNameDesensitize}
 */
public class ChineseNameDesensitization extends AbstractSliderDesensitizationHandler<ChineseNameDesensitize> {

    @Override
    Integer getPrefixKeep(ChineseNameDesensitize annotation) {
        return annotation.prefixKeep();
    }

    @Override
    Integer getSuffixKeep(ChineseNameDesensitize annotation) {
        return annotation.suffixKeep();
    }

    @Override
    String getReplacer(ChineseNameDesensitize annotation) {
        return annotation.replacer();
    }

}
