package com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa las propiedades de perfiles para circuit breakers.
 * Permite mapear múltiples perfiles con sus configuraciones específicas.
 */
@Data
@ConfigurationProperties
public class CircuitBreakerProfileProperties {

    /**
     * Mapa de perfiles, donde la clave es el nombre del perfil y el valor son sus propiedades.
     */
    private Map<String, InstanceProperties> profiles = new HashMap<>();

    /**
     * Obtiene las propiedades de instancia asociadas a la clave proporcionada.
     *
     * @param key nombre del perfil.
     * @return las propiedades de instancia correspondientes, o null si no existe la clave.
     */
    public InstanceProperties getByPropertiesByKey(String key) {
        return profiles.get(key);
    }
}