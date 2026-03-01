package com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.CircuitBreakerProfileProperties;
import com.mercantil.operationsandexecution.crosscutting.restclients.utility.CircuitBreakerConfigBuilder;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.config.circuitbreaker.CircuitBreakerRegistreredEnum;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.CIRCUIT_BREAKER_RELOADED;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Log4j2
public class CircuitBreakerRegistryRefresher {
    /**
     * Registro central de circuit breakers gestionado por Resilience4j.
     */
    private final CircuitBreakerRegistry registry;

    /**
     * Componente encargado de cargar y mantener los perfiles de configuración de circuit breakers.
     */
    private final CircuitBreakerProfilesLoader circuitBreakerProfilesLoader;

    private boolean extracted(CircuitBreakerProfileProperties allProfiles, CircuitBreaker cb) {
        String profileKey = CircuitBreakerRegistreredEnum
                .getProfileKeyByName(cb.getName())
                .getProfileKey()
                .getProfileYamlNotation();
        return ObjectUtils.isNotEmpty(
                allProfiles.getProfiles().get(profileKey)
        );
    }

    /**
     * Tarea programada que recarga la configuración de todos los circuit breakers registrados,
     * aplicando los nuevos perfiles si están disponibles.
     * Se ejecuta diariamente a las 4:00 AM.
     *
     * @return
     */
    @Scheduled(cron = "0 0 4 * * *")
    public Mono<Void> reloadCircuitBreakers() {
        return circuitBreakerProfilesLoader.reloadFromFile()
                .flatMapMany(allProfiles ->
                        Flux.fromIterable(registry.getAllCircuitBreakers())
                                .filter(cb -> extracted(allProfiles, cb))
                                .doOnNext(cb -> updateCircuitBreakerConfig(cb, allProfiles))
                ).then();
    }

    private void updateCircuitBreakerConfig(CircuitBreaker cb, CircuitBreakerProfileProperties allProfiles) {
        var profileKey = CircuitBreakerRegistreredEnum.getProfileKeyByName(cb.getName())
                .getProfileKey()
                .getProfileYamlNotation();

        var newProps = allProfiles.getProfiles().get(profileKey);
        registry.remove(cb.getName());
        registry.addConfiguration(
                cb.getName(),
                CircuitBreakerConfigBuilder.buildEachCircuitBreakerConfig(newProps)
        );
        registry.circuitBreaker(cb.getName());
        log.info(CIRCUIT_BREAKER_RELOADED, cb.getName(), profileKey, newProps);
    }

}