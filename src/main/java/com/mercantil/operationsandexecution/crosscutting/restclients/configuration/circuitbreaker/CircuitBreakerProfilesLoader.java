package com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker;


import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.CircuitBreakerProfileProperties;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.InstanceProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.PROFILES_LOADED_SUCCESS;
import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.PROFILES_LOAD_ERROR;
import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.USING_DEFAULT_PROFILES;
import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.YAML_EMPTY_OR_INVALID;


/**
 * Componente encargado de cargar y mantener en caché los perfiles de configuración
 * para circuit breakers desde un archivo YAML externo.
 * <p>
 * Si ocurre un error durante la carga, se utilizan perfiles de respaldo predefinidos.
 * Permite recargar la configuración de manera reactiva.
 */
@Log4j2
@Component
public class CircuitBreakerProfilesLoader {

    /**
     * Recurso YAML externo con la configuración de circuit breakers.
     */
    @Value("${rest.circuitbreaker.config-file}")
    private Resource resilienceProfilesResource;

    /**
     * Perfiles de circuit breaker actualmente en caché.
     */
    @Getter
    private volatile CircuitBreakerProfileProperties cachedProfiles;

    /**
     * Inicializa la carga de perfiles al arrancar el componente.
     * Si ocurre un error, se usan perfiles de respaldo.
     */
    @PostConstruct
    public void init() {
        internalLoad()
                .doOnNext(profiles -> this.cachedProfiles = profiles)
                .subscribe();
    }

    /**
     * Recarga los perfiles desde el archivo YAML de manera reactiva.
     * Si ocurre un error, se usan perfiles de respaldo.
     *
     * @return Mono con los perfiles cargados o de respaldo.
     */
    public Mono<CircuitBreakerProfileProperties> reloadFromFile() {
        return internalLoad()
                .doOnNext(newProfiles -> this.cachedProfiles = newProfiles);
    }

    /**
     * Realiza la carga interna de los perfiles desde el recurso configurado.
     * Utiliza un scheduler elástico para operaciones de I/O.
     *
     * @return Mono con los perfiles cargados o de respaldo en caso de error.
     */
    private Mono<CircuitBreakerProfileProperties> internalLoad() {
        return Mono.fromCallable(() -> loadProfilesFromResource(resilienceProfilesResource))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(this::handleProfilesLoadError);
    }

    /**
     * Carga los perfiles desde el recurso YAML.
     *
     * @param resource recurso YAML con la configuración.
     * @return Perfiles de circuit breaker cargados.
     * @throws Exception si ocurre un error de lectura o formato.
     */
    private CircuitBreakerProfileProperties loadProfilesFromResource(Resource resource) throws Exception {
        try (InputStream inputStream = resource.getInputStream()) {
            var yaml = new Yaml();
            var profiles = yaml.loadAs(inputStream, CircuitBreakerProfileProperties.class);

            if (ObjectUtils.isEmpty(profiles) || ObjectUtils.isEmpty(profiles.getProfiles())) {
                throw new IllegalStateException(YAML_EMPTY_OR_INVALID);
            }
            log.info(PROFILES_LOADED_SUCCESS, profiles.getProfiles());
            return profiles;
        } catch (Exception e) {
            log.error(PROFILES_LOAD_ERROR, e);
            log.warn(USING_DEFAULT_PROFILES);
            throw e;
        }
    }

    /**
     * Maneja errores durante la carga de perfiles, devolviendo perfiles de respaldo.
     *
     * @param ex excepción ocurrida durante la carga.
     * @return Mono con perfiles de respaldo.
     */
    private Mono<CircuitBreakerProfileProperties> handleProfilesLoadError(Throwable ex) {
        log.error(PROFILES_LOAD_ERROR, ex);
        return Mono.just(createProfilesBackup());
    }

    /**
     * Crea perfiles de respaldo por defecto para circuit breakers.
     *
     * @return Perfiles de respaldo.
     */
    private CircuitBreakerProfileProperties createProfilesBackup() {
        Map<String, InstanceProperties> map = new LinkedHashMap<>();
        map.put("high-critical-service", new InstanceProperties(30, 120, 3, 50, 100));
        map.put("high-non-critical-service", new InstanceProperties(40, 60, 5, 30, 100));
        map.put("medium-critical-service", new InstanceProperties(40, 60, 5, 20, 50));
        map.put("medium-non-critical-service", new InstanceProperties(50, 30, 10, 10, 50));

        CircuitBreakerProfileProperties props = new CircuitBreakerProfileProperties();
        props.setProfiles(map);
        return props;
    }
}