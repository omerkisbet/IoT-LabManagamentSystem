package com.example.demo.Controller;

import com.example.demo.service.ClientIpResolver;
import com.example.demo.service.GeoIpCountryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/localization")
public class LocalizationController {

    private final ClientIpResolver clientIpResolver;
    private final GeoIpCountryService geoIpCountryService;

    public LocalizationController(
            ClientIpResolver clientIpResolver,
            GeoIpCountryService geoIpCountryService
    ) {
        this.clientIpResolver = clientIpResolver;
        this.geoIpCountryService = geoIpCountryService;
    }

    @GetMapping("/default-language")
    public ResponseEntity<Map<String, Object>> getDefaultLanguage(
            HttpServletRequest request
    ) {
        Optional<String> trustedCountryCode =
                clientIpResolver.resolveTrustedCountryCode(request);

        String countryCode = null;
        String source;

        if (trustedCountryCode.isPresent()) {
            countryCode = trustedCountryCode.get();
            source = "country-header";
        } else {
            Optional<InetAddress> clientIp =
                    clientIpResolver.resolveClientIp(request);
            Optional<String> geoIpCountry = clientIp
                    .flatMap(geoIpCountryService::findCountryCode);

            if (geoIpCountry.isPresent()) {
                countryCode = geoIpCountry.get();
                source = "geoip";
            } else {
                source = "accept-language";
            }
        }

        String language;
        if (countryCode != null) {
            language = "TR".equals(countryCode) ? "tr" : "en";
        } else {
            Locale requestLocale = request.getLocale();
            language = requestLocale != null
                    && "tr".equalsIgnoreCase(requestLocale.getLanguage())
                    ? "tr"
                    : "en";
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("language", language);
        response.put("source", source);
        response.put("country", countryCode == null ? "" : countryCode);
        response.put("geoipReady", geoIpCountryService.isReady());

        return ResponseEntity.ok(response);
    }
}
