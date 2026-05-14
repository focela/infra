package com.focela.platform.framework.desensitize.core.base.handler;

import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;

/**
 * Desensitization handler interface
 */
public interface DesensitizationHandler<T extends Annotation> {

    /**
     * Desensitize
     *
     * @param origin     original string
     * @param annotation annotation info
     * @return desensitized string
     */
    String desensitize(String origin, T annotation);

    /**
     * Spring EL expression that indicates whether desensitization is disabled
     *
     * If it returns true, desensitization is skipped
     *
     * @param annotation annotation info
     * @return Spring EL expression indicating whether desensitization is disabled
     */
    default String getDisable(T annotation) {
        // convention: defaults to the enable() attribute. Override in subclasses if it does not apply
        try {
            return (String) ReflectUtil.invoke(annotation, "disable");
        } catch (Exception ex) {
            return "";
        }
    }

}
