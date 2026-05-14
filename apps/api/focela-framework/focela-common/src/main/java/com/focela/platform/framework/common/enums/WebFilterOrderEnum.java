package com.focela.platform.framework.common.enums;

/**
 * Web filter ordering enum, ensuring filters run in the expected order.
 *
 * Every starter needs this utility, so it lives in the common module's enums package.
 */
public interface WebFilterOrderEnum {

    int CORS_FILTER = Integer.MIN_VALUE;

    int TRACE_FILTER = CORS_FILTER + 1;

    int REQUEST_BODY_CACHE_FILTER = Integer.MIN_VALUE + 500;

    int API_ENCRYPT_FILTER = REQUEST_BODY_CACHE_FILTER + 1;

    // OrderedRequestContextFilter defaults to -105, used for i18n context, etc.

    int TENANT_CONTEXT_FILTER = - 104; // Must run before ApiAccessLogFilter

    int API_ACCESS_LOG_FILTER = -103; // Must run after RequestBodyCacheFilter

    int XSS_FILTER = -102;  // Must run after RequestBodyCacheFilter

    // Spring Security Filter defaults to -100; see org.springframework.boot.autoconfigure.security.SecurityProperties

    int TENANT_SECURITY_FILTER = -99; // Must run after Spring Security filter

    int FLOWABLE_FILTER = -98; // Must run after Spring Security filter

}
