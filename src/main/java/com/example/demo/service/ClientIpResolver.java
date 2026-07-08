package com.example.demo.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ClientIpResolver {

    private static final List<String> COUNTRY_HEADERS = List.of(
            "CF-IPCountry",
            "CloudFront-Viewer-Country",
            "X-Vercel-IP-Country",
            "X-Country-Code"
    );

    private static final List<String> SINGLE_IP_HEADERS = List.of(
            "CF-Connecting-IP",
            "True-Client-IP",
            "X-Real-IP"
    );

    private final boolean trustProxyHeaders;

    public ClientIpResolver(
            @Value("${app.localization.trust-proxy-headers:false}")
            boolean trustProxyHeaders
    ) {
        this.trustProxyHeaders = trustProxyHeaders;
    }

    public Optional<String> resolveTrustedCountryCode(
            HttpServletRequest request
    ) {
        if (!trustProxyHeaders) {
            return Optional.empty();
        }

        for (String header : COUNTRY_HEADERS) {
            String value = request.getHeader(header);
            if (value == null) {
                continue;
            }

            String normalized = value.trim().toUpperCase(Locale.ROOT);
            if (normalized.matches("[A-Z]{2}")) {
                return Optional.of(normalized);
            }
        }

        return Optional.empty();
    }

    public Optional<InetAddress> resolveClientIp(HttpServletRequest request) {
        if (trustProxyHeaders) {
            for (String header : SINGLE_IP_HEADERS) {
                Optional<InetAddress> address =
                        parseAddress(request.getHeader(header));
                if (address.isPresent()) {
                    return address;
                }
            }

            Optional<InetAddress> forwardedFor =
                    parseForwardedFor(request.getHeader("X-Forwarded-For"));
            if (forwardedFor.isPresent()) {
                return forwardedFor;
            }

            Optional<InetAddress> forwarded =
                    parseForwardedHeader(request.getHeader("Forwarded"));
            if (forwarded.isPresent()) {
                return forwarded;
            }
        }

        return parseAddress(request.getRemoteAddr());
    }

    private Optional<InetAddress> parseForwardedFor(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        for (String candidate : value.split(",")) {
            Optional<InetAddress> address = parseAddress(candidate);
            if (address.isPresent()) {
                return address;
            }
        }

        return Optional.empty();
    }

    private Optional<InetAddress> parseForwardedHeader(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        for (String element : value.split(",")) {
            for (String parameter : element.split(";")) {
                String trimmed = parameter.trim();
                if (!trimmed.regionMatches(true, 0, "for=", 0, 4)) {
                    continue;
                }

                Optional<InetAddress> address =
                        parseAddress(trimmed.substring(4));
                if (address.isPresent()) {
                    return address;
                }
            }
        }

        return Optional.empty();
    }

    private Optional<InetAddress> parseAddress(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }

        String value = rawValue.trim();
        if (value.startsWith("\"") && value.endsWith("\"")
                && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
        }

        if (value.startsWith("[") && value.contains("]")) {
            value = value.substring(1, value.indexOf(']'));
        } else if (value.matches("\\d{1,3}(?:\\.\\d{1,3}){3}:\\d+")) {
            value = value.substring(0, value.lastIndexOf(':'));
        }

        if (!looksLikeIpLiteral(value)) {
            return Optional.empty();
        }

        try {
            return Optional.of(InetAddress.getByName(value));
        } catch (UnknownHostException exception) {
            return Optional.empty();
        }
    }

    private boolean looksLikeIpLiteral(String value) {
        if (value.contains(":")) {
            return value.matches("[0-9A-Fa-f:.%]+")
                    && !value.equalsIgnoreCase("unknown");
        }

        String[] parts = value.split("\\.", -1);
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            if (!part.matches("\\d{1,3}")) {
                return false;
            }

            int number = Integer.parseInt(part);
            if (number > 255) {
                return false;
            }
        }

        return true;
    }
}
