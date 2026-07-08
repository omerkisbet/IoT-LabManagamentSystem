package com.example.demo.Controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.service.ClientIpResolver;
import com.example.demo.service.GeoIpCountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = LocalizationController.class,
        properties = {
                "app.security.admin.username=admin",
                "app.security.admin.password=Admin123!",
                "app.localization.geoip.enabled=false",
                "app.localization.trust-proxy-headers=true"
        }
)
@Import({
        SecurityConfig.class,
        ClientIpResolver.class,
        GeoIpCountryService.class
})
class LocalizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnTurkishForTurkeyCountryHeader() throws Exception {
        mockMvc.perform(get("/api/localization/default-language")
                        .header("CF-IPCountry", "TR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("tr"))
                .andExpect(jsonPath("$.source").value("country-header"));
    }

    @Test
    void shouldReturnEnglishForForeignCountryHeader() throws Exception {
        mockMvc.perform(get("/api/localization/default-language")
                        .header("CF-IPCountry", "DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("en"))
                .andExpect(jsonPath("$.country").value("DE"));
    }

    @Test
    void shouldUseAcceptLanguageWhenGeoIpIsUnavailable() throws Exception {
        mockMvc.perform(get("/api/localization/default-language")
                        .header("Accept-Language", "tr-TR,tr;q=0.9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("tr"))
                .andExpect(jsonPath("$.source").value("accept-language"))
                .andExpect(jsonPath("$.geoipReady").value(false));
    }
}
