package com.mercantil.operationsandexecution.crosscutting.documentation.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_GROUP;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_CONTACT_EMAIL;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_CONTACT_NAME;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_CONTACT_URL;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_DESCRIPTION;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_LICENCE_NAME;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_LICENCE_URL;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_TERMS_OF_SERVICE;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_TITLE;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_INFO_VERSION;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_PATHS_TO_MATCH;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_PATHS_TO_MATCH_ORIGINAL;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_SCAN_PACKAGE;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_SERVER_DESCRIPTION_APIM;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_SERVER_DESCRIPTION_GATEWAY;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_SERVER_URL_APIM;
import static com.mercantil.operationsandexecution.crosscutting.documentation.constants.SwaggerConstants.UTILITY_OPEN_API_SERVER_URL_GATEWAY;

/**
 * Central OpenAPI/Swagger configuration for the project.
 * <p>
 * Declares the API group, metadata (Info, Contact, License), servers,
 * and an {@link org.springdoc.core.customizers.OpenApiCustomizer} to sanitize/normalize generated paths.
 *
 * @since 1.0
 */
@Configuration
public class SwaggerConfiguration {

    /**
     * Declares an OpenAPI documentation group filtered by package and paths.
     *
     * @return a {@link org.springdoc.core.models.GroupedOpenApi} configured with group name, base package, and paths to scan.
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group(UTILITY_OPEN_API_GROUP)
                .packagesToScan(UTILITY_OPEN_API_SCAN_PACKAGE)
                .pathsToMatch(UTILITY_OPEN_API_PATHS_TO_MATCH)
                .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    /**
     * Exposes the {@link io.swagger.v3.oas.models.OpenAPI} bean with configured servers.
     *
     * @return an {@link io.swagger.v3.oas.models.OpenAPI} instance including server definitions.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().servers(buildApiServers());
    }

    /**
     * Builds the list of API servers to publish in the OpenAPI document.
     *
     * @return an immutable list of {@link io.swagger.v3.oas.models.servers.Server}.
     */
    private List<Server> buildApiServers() {
        return List.of(
                new Server().url(UTILITY_OPEN_API_SERVER_URL_APIM)
                        .description(UTILITY_OPEN_API_SERVER_DESCRIPTION_APIM),
                new Server().url(UTILITY_OPEN_API_SERVER_URL_GATEWAY)
                        .description(UTILITY_OPEN_API_SERVER_DESCRIPTION_GATEWAY)
        );
    }

    /**
     * Customizes the OpenAPI document by adding metadata (Info) and
     * normalizing generated paths.
     *
     * @return an {@link org.springdoc.core.customizers.OpenApiCustomizer} applied to the document.
     */
    private OpenApiCustomizer openApiCustomizer() {
        return openAPI -> {
            openAPI.info(buildApiInfo());

            if (openAPI.getPaths() != null) {
                openAPI.setPaths(sanitizePaths(openAPI));
            }
        };
    }

    /**
     * Builds the {@link io.swagger.v3.oas.models.info.Info} section of the OpenAPI document:
     * title, description, version, terms of service, contact, and license.
     *
     * @return a fully populated {@link io.swagger.v3.oas.models.info.Info} instance.
     */
    private Info buildApiInfo() {
        return new Info()
                .title(UTILITY_OPEN_API_INFO_TITLE)
                .description(UTILITY_OPEN_API_INFO_DESCRIPTION)
                .version(UTILITY_OPEN_API_INFO_VERSION)
                .termsOfService(UTILITY_OPEN_API_INFO_TERMS_OF_SERVICE)
                .contact(buildContact())
                .license(buildLicense());
    }

    /**
     * Builds the API provider contact information.
     *
     * @return a {@link io.swagger.v3.oas.models.info.Contact} with name, URL, and email.
     */
    private Contact buildContact() {
        return new Contact()
                .name(UTILITY_OPEN_API_INFO_CONTACT_NAME)
                .url(UTILITY_OPEN_API_INFO_CONTACT_URL)
                .email(UTILITY_OPEN_API_INFO_CONTACT_EMAIL);
    }

    /**
     * Builds the API license information.
     *
     * @return a {@link io.swagger.v3.oas.models.info.License} with name and URL.
     */
    private License buildLicense() {
        return new License()
                .name(UTILITY_OPEN_API_INFO_LICENCE_NAME)
                .url(UTILITY_OPEN_API_INFO_LICENCE_URL);
    }

    /**
     * Normalizes the OpenAPI paths by removing the
     * {@code UTILITY_OPEN_API_PATHS_TO_MATCH_ORIGINAL} prefix when present.
     * <p>
     * This avoids duplicated prefixes or undesired paths in the Swagger UI.
     *
     * @param openAPI the current document to process (non-null and with paths).
     * @return new {@link io.swagger.v3.oas.models.Paths} with sanitized routes.
     */
    private Paths sanitizePaths(OpenAPI openAPI) {
        Paths originalPaths = openAPI.getPaths();
        Paths updatedPaths = new Paths();

        originalPaths.forEach((originalPath, pathItem) -> {
            if (originalPath.startsWith(UTILITY_OPEN_API_PATHS_TO_MATCH_ORIGINAL)) {
                String newPath = originalPath.replaceFirst(UTILITY_OPEN_API_PATHS_TO_MATCH_ORIGINAL, "");
                updatedPaths.addPathItem(newPath, pathItem);
            } else {
                updatedPaths.addPathItem(originalPath, pathItem);
            }
        });

        return updatedPaths;
    }

}
