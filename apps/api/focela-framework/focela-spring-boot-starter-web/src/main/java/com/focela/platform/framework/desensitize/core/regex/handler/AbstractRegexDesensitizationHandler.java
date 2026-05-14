package com.focela.platform.framework.desensitize.core.regex.handler;

import com.focela.platform.framework.common.utils.spring.SpringExpressionUtils;
import com.focela.platform.framework.desensitize.core.base.handler.DesensitizationHandler;

import java.lang.annotation.Annotation;

/**
 * Abstract regex desensitization handler with common methods implemented
 */
public abstract class AbstractRegexDesensitizationHandler<T extends Annotation>
        implements DesensitizationHandler<T> {

    @Override
    public String desensitize(String origin, T annotation) {
        // 1. check whether desensitization is disabled
        Object disable = SpringExpressionUtils.parseExpression(getDisable(annotation));
        if (Boolean.TRUE.equals(disable)) {
            return origin;
        }

        // 2. perform desensitization
        String regex = getRegex(annotation);
        String replacer = getReplacer(annotation);
        return origin.replaceAll(regex, replacer);
    }

    /**
     * Get the regex parameter from the annotation
     *
     * @param annotation annotation info
     * @return regular expression
     */
    abstract String getRegex(T annotation);

    /**
     * Get the replacer parameter from the annotation
     *
     * @param annotation annotation info
     * @return replacement string
     */
    abstract String getReplacer(T annotation);

}
