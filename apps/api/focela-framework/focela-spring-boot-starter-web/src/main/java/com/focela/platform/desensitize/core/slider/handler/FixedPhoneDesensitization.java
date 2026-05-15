package com.focela.platform.desensitize.core.slider.handler;

import com.focela.platform.desensitize.core.slider.annotation.FixedPhoneDesensitize;

/**
 * Desensitization handler for {@link FixedPhoneDesensitize}
 */
public class FixedPhoneDesensitization extends AbstractSliderDesensitizationHandler<FixedPhoneDesensitize> {

    @Override
    Integer getPrefixKeep(FixedPhoneDesensitize annotation) {
        return annotation.prefixKeep();
    }

    @Override
    Integer getSuffixKeep(FixedPhoneDesensitize annotation) {
        return annotation.suffixKeep();
    }

    @Override
    String getReplacer(FixedPhoneDesensitize annotation) {
        return annotation.replacer();
    }

}
