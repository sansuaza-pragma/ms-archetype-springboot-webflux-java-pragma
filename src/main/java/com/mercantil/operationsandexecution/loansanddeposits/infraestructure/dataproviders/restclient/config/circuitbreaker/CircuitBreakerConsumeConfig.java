package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.config.circuitbreaker;

import com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker.CircuitBreakerProfilesLoader;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.InstanceProperties;
import com.mercantil.operationsandexecution.crosscutting.restclients.utility.CircuitBreakerConfigBuilder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class CircuitBreakerConsumeConfig {
    private static final String CIRCUIT_BREAKER_STATE_TRANSITION = "Circuit breaker state transition - Name: %s, From: %s, To: %s";

    private final CircuitBreakerProfilesLoader profilesLoader;

    @Bean
    public CircuitBreaker entrustCB(CircuitBreakerRegistry registry) {

        String profileKey = CircuitBreakerRegistreredEnum.DATA_PRO_RETRIEVE_CARD_TRANSACTION
                .getProfileKey()
                .getProfileYamlNotation();

        InstanceProperties props = profilesLoader.getCachedProfiles()
                .getByPropertiesByKey(profileKey);

        log.info("Inicializando Circuit Breaker: {} con perfil: {}",
                CircuitBreakerRegistreredEnum.DATA_PRO_RETRIEVE_CARD_TRANSACTION.getInternalName(),
                profileKey);

        CircuitBreaker circuitBreaker = registry.circuitBreaker(
                CircuitBreakerRegistreredEnum.DATA_PRO_RETRIEVE_CARD_TRANSACTION.getInternalName(),
                CircuitBreakerConfigBuilder.buildEachCircuitBreakerConfig(props)
        );


        circuitBreaker.getEventPublisher()
                .onStateTransition(this::logCircuitBreakerStateChange);

        return circuitBreaker;
    }


    private void logCircuitBreakerStateChange(CircuitBreakerOnStateTransitionEvent event) {
        String message = String.format(CIRCUIT_BREAKER_STATE_TRANSITION,
                event.getCircuitBreakerName(),
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState());

        log.info("SYSTEM");
        log.warn(message);
    }
}