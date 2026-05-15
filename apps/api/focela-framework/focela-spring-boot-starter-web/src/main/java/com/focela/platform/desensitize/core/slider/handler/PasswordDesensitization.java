package com.focela.platform.desensitize.core.slider.handler;

import com.focela.platform.desensitize.core.slider.annotation.PasswordDesensitize;

/**
 * Desensitization handler for {@link PasswordDesensitize}
 */
public class PasswordDesensitization extends AbstractSliderDesensitizationHandler<PasswordDesensitize> {
    @Override
    Integer getPrefixKeep(PasswordDesensitize annotation) {
        return annotation.prefixKeep();
    }

    @Override
    Integer getSuffixKeep(PasswordDesensitize annotation) {
        return annotation.suffixKeep();
    }

    @Override
    String getReplacer(PasswordDesensitize annotation) {
        return annotation.replacer();
    }

}
