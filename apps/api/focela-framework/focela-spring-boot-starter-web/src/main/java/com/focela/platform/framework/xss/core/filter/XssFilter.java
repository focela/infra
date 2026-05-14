package com.focela.platform.framework.xss.core.filter;

import com.focela.platform.framework.xss.config.XssProperties;
import com.focela.platform.framework.xss.core.clean.XssCleaner;
import lombok.AllArgsConstructor;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * XSS filter
 */
@AllArgsConstructor
public class XssFilter extends OncePerRequestFilter {

    /**
     * Properties
     */
    private final XssProperties properties;
    /**
     * Path matcher
     */
    private final PathMatcher pathMatcher;

    private final XssCleaner xssCleaner;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // do not filter if disabled
        if (!properties.isEnable()) {
            return true;
        }

        // do not filter if matched to an exclusion
        String uri = request.getRequestURI();
        return properties.getExcludeUrls().stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri));
    }

}
