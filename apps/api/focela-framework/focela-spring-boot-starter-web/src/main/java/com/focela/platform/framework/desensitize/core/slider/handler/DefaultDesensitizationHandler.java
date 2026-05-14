package com.focela.platform.framework.desensitize.core.slider.handler;

import com.focela.platform.framework.desensitize.core.slider.annotation.SliderDesensitize;

/**
 * Desensitization handler for {@link SliderDesensitize}
 */
public class DefaultDesensitizationHandler extends AbstractSliderDesensitizationHandler<SliderDesensitize> {

    @Override
    Integer getPrefixKeep(SliderDesensitize annotation) {
        return annotation.prefixKeep();
    }

    @Override
    Integer getSuffixKeep(SliderDesensitize annotation) {
        return annotation.suffixKeep();
    }

    @Override
    String getReplacer(SliderDesensitize annotation) {
        return annotation.replacer();
    }

}
