package com.focela.platform.framework.desensitize.core.slider.handler;

import com.focela.platform.framework.common.utils.spring.SpringExpressionUtils;
import com.focela.platform.framework.desensitize.core.base.handler.DesensitizationHandler;

import java.lang.annotation.Annotation;

/**
 * Abstract slider desensitization handler with common methods implemented
 */
public abstract class AbstractSliderDesensitizationHandler<T extends Annotation>
        implements DesensitizationHandler<T> {

    @Override
    public String desensitize(String origin, T annotation) {
        // 1. check whether desensitization is disabled
        Object disable = SpringExpressionUtils.parseExpression(getDisable(annotation));
        if (Boolean.TRUE.equals(disable)) {
            return origin;
        }

        // 2. perform desensitization
        int prefixKeep = getPrefixKeep(annotation);
        int suffixKeep = getSuffixKeep(annotation);
        String replacer = getReplacer(annotation);
        int length = origin.length();
        int interval = length - prefixKeep - suffixKeep;

        // case 1: original string length <= prefix+suffix kept length, replace the whole string
        if (interval <= 0) {
            return buildReplacerByLength(replacer, length);
        }

        // case 2: original string length > prefix+suffix kept length, replace the middle portion
        return origin.substring(0, prefixKeep) +
                buildReplacerByLength(replacer, interval) +
                origin.substring(prefixKeep + interval);
    }

    /**
     * Build the replacer by repeating it for the given length
     *
     * @param replacer replacer
     * @param length   length
     * @return built replacer
     */
    private String buildReplacerByLength(String replacer, int length) {
        return replacer.repeat(length);
    }

    /**
     * Prefix kept length
     *
     * @param annotation annotation info
     * @return prefix kept length
     */
    abstract Integer getPrefixKeep(T annotation);

    /**
     * Suffix kept length
     *
     * @param annotation annotation info
     * @return suffix kept length
     */
    abstract Integer getSuffixKeep(T annotation);

    /**
     * Replacer
     *
     * @param annotation annotation info
     * @return replacer
     */
    abstract String getReplacer(T annotation);

}
