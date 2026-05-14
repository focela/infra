package com.focela.platform.framework.desensitize.core.slider.handler;

import com.focela.platform.framework.desensitize.core.slider.annotation.MobileDesensitize;

/**
 * Desensitization handler for {@link MobileDesensitize}
 */
public class MobileDesensitization extends AbstractSliderDesensitizationHandler<MobileDesensitize> {

    @Override
    Integer getPrefixKeep(MobileDesensitize annotation) {
        return annotation.prefixKeep();
    }

    @Override
    Integer getSuffixKeep(MobileDesensitize annotation) {
        return annotation.suffixKeep();
    }

    @Override
    String getReplacer(MobileDesensitize annotation) {
        return annotation.replacer();
    }

}
