package com.mercantil.operationsandexecution.crosscutting.restclients.utility;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.StandardRestException;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.InstanceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.experimental.UtilityClass;

import java.time.Duration;

/**
 * Clase utilitaria para construir configuraciones personalizadas de circuit breaker
 * a partir de las propiedades definidas en {@link InstanceProperties}.
 */
@UtilityClass
public class CircuitBreakerConfigBuilder {

    /**
     * Construye una configuración de circuit breaker usando los parámetros proporcionados.
     *
     * @param instanceProperties propiedades de la instancia del circuit breaker.
     * @return configuración personalizada de {@link CircuitBreakerConfig}.
     */
    public static CircuitBreakerConfig buildEachCircuitBreakerConfig(InstanceProperties instanceProperties) {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(instanceProperties.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofSeconds(instanceProperties.getWaitDurationInOpenState()))
                .slidingWindowSize(instanceProperties.getSlidingWindowSize())
                .minimumNumberOfCalls(instanceProperties.getMinimumNumberOfCalls())
                .permittedNumberOfCallsInHalfOpenState(instanceProperties.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .recordException(CircuitBreakerConfigBuilder::isServerErrorException)
                .build();
    }

    /**
     * Determina si la excepción debe ser registrada como error del servidor.
     *
     * @param throwable excepción lanzada.
     * @return {@code true} si es un error 5xx, {@code true} para otras excepciones.
     */
    private static boolean isServerErrorException(Throwable throwable) {
        if (throwable instanceof StandardRestException ex) {
            return ex.getStatusCode().is5xxServerError();
        }
        return true;
    }
}