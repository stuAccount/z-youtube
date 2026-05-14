package com.zyoutube.common.context;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RequestContextProvider {
    private final HttpServletRequest request;

    public String getCurrentRequestIpOrNull() {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public String getCurrentSessionIdOrNull() {
        HttpSession session = request.getSession(false);
        return session != null ? session.getId() : null;
    }

    public String getCurrentRequestUserAgentOrNull() {
        return request.getHeader("User-Agent");
    }
}
