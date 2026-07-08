package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ClientIpResolverTest {

    @Test
    void shouldIgnoreProxyHeadersWhenTheyAreNotTrusted() {
        ClientIpResolver resolver = new ClientIpResolver(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("203.0.113.10");
        request.addHeader("CF-IPCountry", "TR");
        request.addHeader("CF-Connecting-IP", "8.8.8.8");

        assertThat(resolver.resolveTrustedCountryCode(request)).isEmpty();
        assertThat(resolver.resolveClientIp(request))
                .hasValueSatisfying(address ->
                        assertThat(address.getHostAddress())
                                .isEqualTo("203.0.113.10"));
    }

    @Test
    void shouldReadTrustedProxyCountryAndIpHeaders() {
        ClientIpResolver resolver = new ClientIpResolver(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("172.18.0.1");
        request.addHeader("CF-IPCountry", "tr");
        request.addHeader("CF-Connecting-IP", "8.8.8.8");

        assertThat(resolver.resolveTrustedCountryCode(request))
                .contains("TR");
        assertThat(resolver.resolveClientIp(request))
                .hasValueSatisfying(address ->
                        assertThat(address.getHostAddress())
                                .isEqualTo("8.8.8.8"));
    }
}
