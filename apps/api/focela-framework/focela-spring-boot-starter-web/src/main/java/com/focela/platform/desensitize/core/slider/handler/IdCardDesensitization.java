package com.focela.platform.desensitize.core.slider.handler;

import com.focela.platform.desensitize.core.slider.annotation.IdCardDesensitize;

/**
 * Desensitization handler for {@link IdCardDesensitize}
 */
public class IdCardDesensitization extends AbstractSliderDesensitizationHandler<IdCardDesensitize> {
    @Override
    Integer getPrefixKeep(IdCardDesensitize annotation) {
        return annotation.prefixKeep();
    }

    @Override
    Integer getSuffixKeep(IdCardDesensitize annotation) {
        return annotation.suffixKeep();
    }

    @Override
    String getReplacer(IdCardDesensitize annotation) {
        return annotation.replacer();
    }

}
