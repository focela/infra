package com.focela.platform.framework.xss.core.clean;

/**
 * Cleans XSS-risk data from HTML text
 */
public interface XssCleaner {

    /**
     * Clean XSS-risky text
     *
     * @param html original HTML
     * @return cleaned HTML
     */
    String clean(String html);

}
