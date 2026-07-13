package com.example.demo.config;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.security.admin.username}") String username,
            @Value("${app.security.admin.password}") String password
    ) {
        UserDetails admin = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper
    ) throws Exception {

        http
                /*
                 * The application currently uses HTTP Basic authentication
                 * and does not rely on cookie-based sessions.
                 */
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(authorize -> authorize

                        /*
                         * Public pages and static resources.
                         */
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/student.html",
                                "/academic.html",
                                "/news.html",
                                "/publications.html",
                                "/projects.html",
                                "/team.html",
                                "/admin.html",
                                "/favicon.ico",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        /*
                         * Swagger documentation is restricted to admins.
                         */
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).hasRole("ADMIN")

                        /*
                         * Dokku and Actuator health checks.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/actuator/health",
                                "/actuator/health/**"
                        ).permitAll()

                        /*
                         * Public read-only API endpoints.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/students/**",
                                "/api/activities/**",
                                "/api/projects/**",
                                "/api/publications/**",
                                "/api/news/**",
                                "/api/media/files/**",
                                "/api/localization/**"
                        ).permitAll()

                        /*
                         * The public contact form can create messages.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/contact-messages"
                        ).permitAll()

                        /*
                         * Every other request requires the ADMIN role.
                         */
                        .anyRequest().hasRole("ADMIN")
                )

                .httpBasic(Customizer.withDefaults())

                /*
                 * Return standardized JSON responses for 401 and 403 errors.
                 */
                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authenticationException) ->
                                        writeSecurityResponse(
                                                response,
                                                objectMapper,
                                                HttpStatus.UNAUTHORIZED,
                                                "Authentication is required.",
                                                request.getRequestURI()
                                        )
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        writeSecurityResponse(
                                                response,
                                                objectMapper,
                                                HttpStatus.FORBIDDEN,
                                                "You do not have permission to perform this operation.",
                                                request.getRequestURI()
                                        )
                        )
                );

        return http.build();
    }

    private void writeSecurityResponse(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            HttpStatus status,
            String message,
            String path
    ) throws IOException {

        Map<String, Object> responseBody = new LinkedHashMap<>();

        responseBody.put(
                "timestamp",
                LocalDateTime.now()
        );

        responseBody.put(
                "status",
                status.value()
        );

        responseBody.put(
                "error",
                status.getReasonPhrase()
        );

        responseBody.put(
                "message",
                message
        );

        responseBody.put(
                "path",
                path
        );

        response.setStatus(status.value());

        if (status == HttpStatus.UNAUTHORIZED) {
            response.setHeader(
                    "WWW-Authenticate",
                    "Basic realm=\"Laboratory API\""
            );
        }

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                responseBody
        );
    }
} 