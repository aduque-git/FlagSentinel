package com.example.flagsentinelapi.middleware;

import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class HttpLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingInterceptor.class);
    private static final String START_TIME = "startTime";
    private static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        request.setAttribute(START_TIME, System.nanoTime());

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.HTTP_REQUEST_START,
                request.getMethod(),
                request.getRequestURI()
        ));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        Long start = (Long) request.getAttribute(START_TIME);
        long durationMs = (start != null) ? (System.nanoTime() - start) / 1_000_000 : -1;

        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        String ip = getClientIp(request);
        String traceId = request.getHeader(TRACE_HEADER);

        String user = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            user = auth.getName();
        }

        if (ex != null || status >= 500) {
            log.error(ApiLogMessages.get(
                    LogPropertiesKeys.HTTP_REQUEST_ERROR,
                    method, uri, status, user, ip, durationMs, traceId
            ), ex);

        } else if (status >= 400) {
            log.warn(ApiLogMessages.get(
                    LogPropertiesKeys.HTTP_REQUEST_WARNING,
                    method, uri, status, user, ip, durationMs, traceId
            ));

        } else {
            log.info(ApiLogMessages.get(
                    LogPropertiesKeys.HTTP_REQUEST_COMPLETED,
                    method, uri, status, user, ip, durationMs, traceId
            ));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}