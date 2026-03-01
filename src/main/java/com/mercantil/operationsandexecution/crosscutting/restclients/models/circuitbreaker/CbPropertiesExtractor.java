package com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum que define los perfiles de configuración para circuit breakers,
 * asociando cada uno con su notación correspondiente en el archivo YAML.
 */
@Getter
@RequiredArgsConstructor
public enum CbPropertiesExtractor {
    HIGH_CRITICAL("critical-intermittent-service"),
    HIGH_NON_CRITICAL("high-non-critical-service"),
    MEDIUM_CRITICAL("medium-critical-service"),
    MEDIUM_NON_CRITICAL("medium-non-critical-service");

    /**
     * Notación utilizada en el archivo YAML para identificar el perfil.
     */
    private final String profileYamlNotation;
}