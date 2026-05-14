package com.focela.platform.framework.tracer.core.filter;

import com.focela.platform.framework.common.utils.monitor.TracerUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Trace filter that writes the traceId to the response header.
 */
public class TraceFilter extends OncePerRequestFilter {

    /**
     * Header name - trace ID
     */
    private static final String HEADER_NAME_TRACE_ID = "trace-id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Set the traceId on the response
        response.addHeader(HEADER_NAME_TRACE_ID, TracerUtils.getTraceId());
        // Continue the filter chain
        chain.doFilter(request, response);
    }

}
