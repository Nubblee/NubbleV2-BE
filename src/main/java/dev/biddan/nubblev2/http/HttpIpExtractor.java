package dev.biddan.nubblev2.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class HttpIpExtractor {

    public String getClientIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("Forwarded");
        if (forwarded != null && !forwarded.isEmpty()) {
            String clientIp = parseForwardedHeader(forwarded);
            if (clientIp != null && !clientIp.isEmpty()) {
                return clientIp;
            }
        }

        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    private String parseForwardedHeader(String forwarded) {
        String[] parts = forwarded.split(",");

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.startsWith("for=")) {
                return trimmed.substring(4);
            }
        }

        return null;
    }
}
