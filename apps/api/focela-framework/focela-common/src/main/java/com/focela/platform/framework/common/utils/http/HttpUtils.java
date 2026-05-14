package com.focela.platform.framework.common.utils.http;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP utility class
 */
public class HttpUtils {

    /**
     * URL-encode a parameter.
     *
     * @param value parameter
     * @return encoded parameter
     */
    public static String encodeUtf8(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * URL-decode a query parameter.
     * Note: this method decodes + as space, which is suitable for query parameters but not for URL paths.
     *
     * @see #decodeUrlPath(String)
     * @param value parameter
     * @return decoded parameter
     */
    public static String decodeUtf8(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    /**
     * URL-decode a URL path.
     * Unlike {@link #decodeUtf8(String)}, this method does not decode + into space and keeps + literal.
     * Suitable for decoding the path segment of a URL.
     *
     * @param path URL path
     * @return decoded path
     */
    public static String decodeUrlPath(String path) {
        // first replace + with %2B so URLDecoder does not turn it into a space
        String encoded = path.replace("+", "%2B");
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    public static String replaceUrlQuery(String url, String key, String value) {
        UrlBuilder builder = UrlBuilder.of(url, Charset.defaultCharset());
        // remove first
        TableMap<CharSequence, CharSequence> query = (TableMap<CharSequence, CharSequence>)
                ReflectUtil.getFieldValue(builder.getQuery(), "query");
        query.remove(key);
        // then add
        builder.addQuery(key, value);
        return builder.build();
    }

    public static String removeUrlQuery(String url) {
        if (!StrUtil.contains(url, '?')) {
            return url;
        }
        UrlBuilder builder = UrlBuilder.of(url, Charset.defaultCharset());
        // remove query and fragment
        builder.setQuery(null);
        builder.setFragment(null);
        return builder.build();
    }

    /**
     * Append parameters to a URL.
     *
     * Copied from the {@code append} method of Spring Security OAuth2's AuthorizationEndpoint.
     *
     * @param base     base URL
     * @param query    query parameters
     * @param keys     mapping from query keys to their actual key names; e.g. if a query has key "xx" but the actual key is
     *                 "extra_xx", add that mapping in keys
     * @param fragment whether to put values in the URL fragment (after #)
     * @return the assembled URL
     */
    public static String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {
        UriComponentsBuilder template = UriComponentsBuilder.newInstance();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
        URI redirectUri;
        try {
            // assume it's encoded to start with (if it came in over the wire)
            redirectUri = builder.build(true).toUri();
        } catch (Exception e) {
            // ... but allow client registrations to contain hard-coded non-encoded values
            redirectUri = builder.build().toUri();
            builder = UriComponentsBuilder.fromUri(redirectUri);
        }
        template.scheme(redirectUri.getScheme()).port(redirectUri.getPort()).host(redirectUri.getHost())
                .userInfo(redirectUri.getUserInfo()).path(redirectUri.getPath());

        if (fragment) {
            StringBuilder values = new StringBuilder();
            if (redirectUri.getFragment() != null) {
                String append = redirectUri.getFragment();
                values.append(append);
            }
            for (String key : query.keySet()) {
                if (values.length() > 0) {
                    values.append("&");
                }
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                values.append(name).append("={").append(key).append("}");
            }
            if (values.length() > 0) {
                template.fragment(values.toString());
            }
            UriComponents encoded = template.build().expand(query).encode();
            builder.fragment(encoded.getFragment());
        } else {
            for (String key : query.keySet()) {
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                template.queryParam(name, "{" + key + "}");
            }
            template.fragment(redirectUri.getFragment());
            UriComponents encoded = template.build().expand(query).encode();
            builder.query(encoded.getQuery());
        }
        return builder.build().toUriString();
    }

    public static String[] obtainBasicAuthorization(HttpServletRequest request) {
        String clientId;
        String clientSecret;
        // first read from Header
        String authorization = request.getHeader("Authorization");
        authorization = StrUtil.subAfter(authorization, "Basic ", true);
        if (StringUtils.hasText(authorization)) {
            authorization = Base64.decodeStr(authorization);
            clientId = StrUtil.subBefore(authorization, ":", false);
            clientSecret = StrUtil.subAfter(authorization, ":", false);
            // otherwise read from request parameters
        } else {
            clientId = request.getParameter("client_id");
            clientSecret = request.getParameter("client_secret");
        }

        // return only when both are present
        if (StrUtil.isNotEmpty(clientId) && StrUtil.isNotEmpty(clientSecret)) {
            return new String[]{clientId, clientSecret};
        }
        return null;
    }

    /**
     * HTTP POST request, based on {@link cn.hutool.http.HttpUtil}.
     *
     * This wrapper exists because the default HttpUtil shortcut does not accept a headers parameter.
     *
     * @param url         URL
     * @param headers     request headers
     * @param requestBody request body
     * @return response body
     */
    public static String post(String url, Map<String, String> headers, String requestBody) {
        try (HttpResponse response = HttpRequest.post(url)
                .addHeaders(headers)
                .body(requestBody)
                .execute()) {
            return response.body();
        }
    }

    /**
     * HTTP GET request, based on {@link cn.hutool.http.HttpUtil}.
     *
     * This wrapper exists because the default HttpUtil shortcut does not accept a headers parameter.
     *
     * @param url     URL
     * @param headers request headers
     * @return response body
     */
    public static String get(String url, Map<String, String> headers) {
        try (HttpResponse response = HttpRequest.get(url)
                .addHeaders(headers)
                .execute()) {
            return response.body();
        }
    }

}
