package com.focela.platform.framework.common.utils.spring;

import cn.hutool.extra.spring.SpringUtil;

import java.util.Objects;

/**
 * Spring utility class
 */
public class SpringUtils extends SpringUtil {

    /**
     * Whether running in the production environment.
     *
     * @return true if production
     */
    public static boolean isProd() {
        String activeProfile = getActiveProfile();
        return Objects.equals("prod", activeProfile);
    }

}
