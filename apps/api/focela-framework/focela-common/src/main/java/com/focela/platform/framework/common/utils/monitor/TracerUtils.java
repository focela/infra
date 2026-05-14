package com.focela.platform.framework.common.utils.monitor;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * Tracing utility.
 *
 * Every starter needs this utility, so it lives in the common module's util package.
 */
public class TracerUtils {

    /**
     * Private constructor.
     */
    private TracerUtils() {
    }

    /**
     * Get the trace ID; returns SkyWalking's TraceId directly.
     * Returns an empty string when absent.
     *
     * @return trace ID
     */
    public static String getTraceId() {
        return TraceContext.traceId();
    }

}
