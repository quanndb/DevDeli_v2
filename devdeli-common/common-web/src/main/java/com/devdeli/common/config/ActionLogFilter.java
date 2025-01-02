package com.devdeli.common.config;

import com.devdeli.common.support.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@WebFilter("/api/**")
@Slf4j
@Order(100)
@Component
public class ActionLogFilter extends OncePerRequestFilter {
    private static final int START_LOG_HTTP_STATUS = HttpStatus.BAD_REQUEST.value();
    private static final String LOG_REMOTE_IP = "remote_ip";
    private static final String LOG_USERNAME = "username";
    private static final String LOG_CLIENT_MESSAGE_ID = "client_message_id";

    private static final List<String> SENSITIVE_KEYS = List.of(
            "password",
            "currentPassword",
            "newPassword",
            "token",
            "accessToken",
            "refreshToken",
            "access_token",
            "refresh_token"
    );
    private final List<String> blackList =
            List.of(
                    "/api/\\d+\\.\\d+\\.\\d+/certificate/.well-known/jwks\\.json",
                    ".*/actuator/.*",
                    "/api/.*-logs.*",
                    "/swagger-ui.*",
                    "/api/.*/swagger-ui/.*",
                    "/swagger-resources.*",
                    ".*/v3/api-docs.*",
                    ".*/integrations/files/upload",
                    "/api/\\d+\\.\\d+\\.\\d+/swagger-ui/.*",
                    "/api/\\d+\\.\\d+\\.\\d+/v3/api-docs",
                    "/api/\\d+\\.\\d+\\.\\d+/v3/api-docs/.*"
            );
    private final List<String> blackListMimeType =
            List.of("multipart\\/form-data.*", "image\\/.*", "application\\/octet-stream.*");

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        Instant start = Instant.now();
        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(servletRequest);
        requestWrapper.setCharacterEncoding("UTF-8");

        ContentCachingResponseWrapper cachedResponse =
                new ContentCachingResponseWrapper(servletResponse);
        cachedResponse.setCharacterEncoding("UTF-8");

        // Check if the URI matches any entry in the blackList
        String uri = servletRequest.getRequestURI();
        if (blackList.stream().anyMatch(uri::matches)) {
            filterChain.doFilter(servletRequest, servletResponse); // Skip logging
            return;
        }

        // Check if the MIME type matches any entry in the blackListMimeType
        String requestContentType = servletRequest.getHeader("Content-Type");
        if (requestContentType != null && blackListMimeType.stream().anyMatch(requestContentType::matches)) {
            filterChain.doFilter(servletRequest, servletResponse); // Skip logging
            return;
        }

        // Proceed with logging for non-blacklisted URIs and MIME types
        String clientMessageId = servletRequest.getHeader(LOG_CLIENT_MESSAGE_ID);
        String remoteIp = getRemoteIp(servletRequest);
        try {
            MDC.put(LOG_REMOTE_IP, remoteIp);
            MDC.put(LOG_USERNAME, SecurityUtils.getCurrentUser().orElse("anonymous"));
            MDC.put(LOG_CLIENT_MESSAGE_ID, clientMessageId);

            filterChain.doFilter(requestWrapper, cachedResponse); // Ensure the filter is applied to the requestWrapper and cachedResponse

            Instant finishRequest = Instant.now();

            String responseContentType = cachedResponse.getHeader("Content-Type");
            boolean ignoredResponse =
                    responseContentType != null
                            && blackListMimeType.stream().anyMatch(responseContentType::matches);

            if (!ignoredResponse) {
                logRequestDetails(requestWrapper);
                logResponseDetails(cachedResponse);
            }

            if (cachedResponse.getStatus() >= START_LOG_HTTP_STATUS) {
                long time = Duration.between(start, finishRequest).toMillis();
                log.info("Request processed in {} ms", time);
            }

            cachedResponse.copyBodyToResponse(); // Make sure response body is copied back to the response

        } finally {
            MDC.remove(LOG_REMOTE_IP);
            MDC.remove(LOG_USERNAME);
            MDC.remove(LOG_CLIENT_MESSAGE_ID);
        }
    }

    private void logRequestDetails(ContentCachingRequestWrapper request) {
        try {
            String requestBody = new String(request.getContentAsByteArray(), request.getCharacterEncoding());
            String maskedBody = maskSensitiveFields(requestBody);

            log.info("---------------------------------------------------------------------");
            log.info("Request URI: {}", request.getRequestURI());
            log.info("Request Method: {}", request.getMethod());
            log.info("Request Headers: {}", getHeadersAsString(request));
            log.info("Request Body: {}", maskedBody);
        } catch (Exception e) {
            log.error("Error logging request details", e);
        }
    }

    private void logResponseDetails(ContentCachingResponseWrapper response) {
        try {
            String responseBody = new String(response.getContentAsByteArray(), response.getCharacterEncoding());
            String maskedBody = maskSensitiveFields(responseBody);
            log.info("Response Status: {}", response.getStatus());
            log.info("Response Headers: {}", response.getHeaderNames());
            log.info("Response Body: {}", maskedBody);
        } catch (Exception e) {
            log.error("Error logging response details", e);
        }
        finally {
            log.info("---------------------------------------------------------------------");
        }
    }

    private String getHeadersAsString(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        });
        return headers.toString();
    }

    /**
     * Masks sensitive fields in the JSON body.
     */
    private String maskSensitiveFields(String body) {
        if (!StringUtils.hasLength(body)) {
            return body;
        }
        String maskedBody = body;
        for (String key : SENSITIVE_KEYS) {
            maskedBody = maskedBody.replaceAll(
                    String.format("\"%s\"\\s*:\\s*\"(.*?)\"", key),
                    String.format("\"%s\":\"******\"", key)
            );
        }
        return maskedBody;
    }

    private String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasLength(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                log.info("get remote ip: {}", ip);
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasLength(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
