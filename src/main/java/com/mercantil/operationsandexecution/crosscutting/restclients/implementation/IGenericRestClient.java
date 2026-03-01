package com.mercantil.operationsandexecution.crosscutting.restclients.implementation;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.HttpRequestConfiguration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Abstraction of a generic REST client with optional circuit breaker execution.
 *
 * @since 1.0
 */
public interface IGenericRestClient {

    /**
     * Ejecuta una llamada HTTP sin Circuit Breaker.
     *
     * @param requestConfig configuración completa de la petición
     * @param transactionId identificador de trazabilidad
     * @param uri           endpoint destino
     */
    public <T, R, E> Mono<ResponseEntity<T>> sendRequestAndReceiveResponse(
            HttpRequestConfiguration<T, R, E> requestConfig,
            String transactionId,
            URI uri
    );

    public <T, R, E> Mono<ResponseEntity<T>> sendRequestAndReceiveResponse(
            HttpRequestConfiguration<T, R, E> requestConfig,
            String transactionId,
            URI uri,
            String cbImplemented);

}

