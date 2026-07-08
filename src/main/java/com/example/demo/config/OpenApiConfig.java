package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH_SCHEME =
            "basicAuth";

    @Bean
    public OpenAPI laboratoryOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Laboratory Website API")
                                .version("1.0.0")
                                .description(
                                        "REST API for students, activities, "
                                                + "projects, publications, news, "
                                                + "contact messages and media files."
                                )
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BASIC_AUTH_SCHEME,
                                        new SecurityScheme()
                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )
                                                .scheme("basic")
                                                .description(
                                                        "Administrator username and password."
                                                )
                                )
                );
    }

    @Bean
    public OpenApiCustomizer securityOpenApiCustomizer() {
        return openApi -> {

            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach(
                    (path, pathItem) -> {

                        /*
                         * ContactMessage GET endpoint'leri ADMIN ister.
                         * Diğer GET endpoint'leri public kalır.
                         */
                        if (path.startsWith(
                                "/api/contact-messages"
                        )) {
                            addBasicAuth(
                                    pathItem.getGet()
                            );
                        }

                        /*
                         * ContactMessage oluşturma endpoint'i public.
                         * Diğer POST endpoint'leri ADMIN ister.
                         */
                        if (!path.equals(
                                "/api/contact-messages"
                        )) {
                            addBasicAuth(
                                    pathItem.getPost()
                            );
                        }

                        /*
                         * Bütün değiştirme ve silme işlemleri ADMIN ister.
                         */
                        addBasicAuth(pathItem.getPut());
                        addBasicAuth(pathItem.getPatch());
                        addBasicAuth(pathItem.getDelete());
                    }
            );
        };
    }

    private void addBasicAuth(
            Operation operation
    ) {
        if (operation == null) {
            return;
        }

        operation.addSecurityItem(
                new SecurityRequirement()
                        .addList(BASIC_AUTH_SCHEME)
        );
    }
}