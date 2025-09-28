package com.umtdg.pfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLogInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory
        .getLogger(RequestLogInterceptor.class);

    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler
    )
        throws Exception {
        logger
            .debug(
                "[HANDLER:preHandle][REMOTE:{}] {} {}",
                getRemoteAddr(request),
                request.getMethod(),
                request.getRequestURI()
            );
        return true;
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.isEmpty()) {
            logger.debug("IP from proxy - X-FORWARDED-FOR: {}", ipFromHeader);
            return ipFromHeader;
        }

        return request.getRemoteAddr();
    }
}
