package com.focela.platform.datasource.core.filter;

import com.alibaba.druid.util.Utils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Druid bottom-banner ad removal filter
 */
public class DruidAdRemoveFilter extends OncePerRequestFilter {

    /**
     * Path to common.js
     */
    private static final String COMMON_JS_ILE_PATH = "support/http/resources/js/common.js";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        chain.doFilter(request, response);
        // Reset the buffer; response headers are not reset
        response.resetBuffer();
        // Load common.js
        String text = Utils.readFromResource(COMMON_JS_ILE_PATH);
        // Regex replace the banner to strip the bottom ad block
        text = text.replaceAll("<a.*?banner\"></a><br/>", "");
        text = text.replaceAll("powered.*?shrek.wang</a>", "");
        response.getWriter().write(text);
    }

}
