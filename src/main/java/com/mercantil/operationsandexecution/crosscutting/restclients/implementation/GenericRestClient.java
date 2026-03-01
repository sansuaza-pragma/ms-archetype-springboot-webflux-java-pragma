package com.mercantil.operationsandexecution.crosscutting.restclients.implementation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercantil.operationsandexecution.crosscutting.logging.ILoggerService;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.HttpRequestConfiguration;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.StandardRestException;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.model.ApiException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.CB_OPEN_CODE;
import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.EXTERNAL_SERVICE_UNAVAILABLE;
import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.LOG_CONSUME_WITH_CIRCUIT_BREAKER;
import static com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants.SERVER_TOO_SLOW_MESSAGE;

/**
 * Cliente REST genérico que utiliza WebClient para enviar peticiones HTTP y recibir respuestas.
 * Integra circuit breaker y logging reactivo, mapeando automáticamente los errores HTTP a excepciones estándar personalizadas.
 *
 * @since 1.0
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class GenericRestClient implements IGenericRestClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final ILoggerService loggerService;

    /**
     * Envía una petición HTTP y recibe la respuesta, mapeando errores a excepciones estándar.
     *
     * @param requestConfig Configuración de la petición y tipos de respuesta.
     * @param transactionId Identificador de la transacción.
     * @param uri           URI de destino.
     * @param <T>           Tipo de respuesta esperada.
     * @param <R>           Tipo de error esperado.
     * @param <E>           Tipo de cuerpo de la petición.
     * @return Mono con la respuesta HTTP o error mapeado.
     */
    @Override
    public <T, R, E> Mono<ResponseEntity<T>> sendRequestAndReceiveResponse(
            HttpRequestConfiguration<T, R, E> requestConfig, String transactionId, URI uri) {

        return sendHttpRequest(requestConfig, uri);
    }


    /**
     * Envía una petición HTTP usando circuit breaker, con logging y manejo de errores personalizado.
     *
     * @param requestConfig  Configuración de la petición y tipos de respuesta.
     * @param transactionId  Identificador de la transacción.
     * @param uri            URI de destino.
     * @param cbImplemented  Nombre del circuit breaker a utilizar.
     * @param <T>            Tipo de respuesta esperada.
     * @param <R>            Tipo de error esperado.
     * @param <E>            Tipo de cuerpo de la petición.
     * @return Mono con la respuesta HTTP o error mapeado.
     */
    @Override
    public <T, R, E> Mono<ResponseEntity<T>> sendRequestAndReceiveResponse(
            HttpRequestConfiguration<T, R, E> requestConfig,
            String transactionId,
            URI uri,
            String cbImplemented) {

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(cbImplemented);
        Duration slowCallThreshold = circuitBreaker.getCircuitBreakerConfig().getSlowCallDurationThreshold();

        String logStringFormated = String.format(
                LOG_CONSUME_WITH_CIRCUIT_BREAKER,
                circuitBreaker.getName(),
                circuitBreaker.getState(),
                uri
        );

        return loggerService.logTraceInfo(requestConfig.getAction(), logStringFormated, null)
                .then(sendHttpRequest(requestConfig, uri)
                                .timeout(slowCallThreshold)
                                .onErrorMap(TimeoutException.class, e -> getServerTooSlow(transactionId, e))
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                                .onErrorResume(CallNotPermittedException.class, e
                                        -> getResponseEntityMono(requestConfig.getAction(), transactionId, uri, cbImplemented))
                );
    }

    /**
     * Realiza la petición HTTP y mapea errores a excepciones estándar.
     *
     * @param requestConfig Configuración de la petición.
     * @param uri           URI de destino.
     * @param <T>           Tipo de respuesta esperada.
     * @param <R>           Tipo de error esperado.
     * @param <E>           Tipo de cuerpo de la petición.
     * @return Mono con la respuesta HTTP o error mapeado.
     */
    private <T, R, E> Mono<ResponseEntity<T>> sendHttpRequest(HttpRequestConfiguration<T, R, E> requestConfig, URI uri) {
        return this.webClient
                .method(requestConfig.getMethod())
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.setAll(requestConfig.getHeaders()))
                .bodyValue(ObjectUtils.getIfNull(requestConfig.getRequestBody(), Void.class))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        mapToStandardRestException(resp, requestConfig.getHttpResponsePayload().getErrorResponseType()))
                .toEntity(requestConfig.getHttpResponsePayload().getResponseType());
    }

    /**
     * Devuelve un Mono que emite un error ApiException cuando el circuit breaker está abierto.
     *
     * @param action        Acción de log.
     * @param transactionId Identificador de la transacción.
     * @param uri           URI de destino.
     * @param cbImplemented Nombre del circuit breaker.
     * @param <T>           Tipo de respuesta esperada.
     * @return Mono que emite un error ApiException.
     */
    private <T> Mono<ResponseEntity<T>> getResponseEntityMono(EnumActionLogs action, String transactionId, URI uri, String cbImplemented) {
        return handleFallBack(action, uri.getPath(), transactionId)
                .flatMap(Mono::error);
    }

    /**
     * Maneja el fallback cuando el circuit breaker está abierto, logueando la advertencia y devolviendo la excepción.
     *
     * @param action        Acción de log.
     * @param url           URL de la petición.
     * @param transactionId Identificador de la transacción.
     * @return Mono con la excepción ApiException.
     */
    private Mono<ApiException> handleFallBack(EnumActionLogs action, String url, String transactionId) {
        ApiException exception = new ApiException(
                HttpStatus.BAD_GATEWAY,
                EXTERNAL_SERVICE_UNAVAILABLE.concat(url),
                transactionId,
                EnumActionLogs.GENERIC_ERROR,
                CB_OPEN_CODE
        );
        return loggerService.logWarning(action, exception)
                .thenReturn(exception);
    }

    /**
     * Construye una ApiException para el caso de timeout (servidor muy lento).
     *
     * @param transactionId Identificador de la transacción.
     * @param e             Excepción original.
     * @return ApiException con información de timeout.
     */
    private ApiException getServerTooSlow(String transactionId, Object e) {
        return new ApiException(HttpStatus.GATEWAY_TIMEOUT, SERVER_TOO_SLOW_MESSAGE, transactionId, EnumActionLogs.GENERIC_ERROR, e);
    }

    /**
     * Mapea la respuesta de error HTTP a una excepción estándar personalizada.
     *
     * @param clientResponse    Respuesta HTTP con error.
     * @param errorResponseType Tipo de error esperado.
     * @param <R>               Tipo de error.
     * @return Mono con la excepción mapeada.
     */
    private <R> Mono<? extends Throwable> mapToStandardRestException(ClientResponse clientResponse, Class<R> errorResponseType) {
        return clientResponse.bodyToMono(String.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(buildStandardRestException(clientResponse))
                )).flatMap(body -> Mono.fromCallable(() -> objectMapper.readValue(body, errorResponseType))
                        .map(error -> new StandardRestException(clientResponse.statusCode(), error))
                        .onErrorResume(e -> Mono.just(new StandardRestException(clientResponse.statusCode(), body)))
                        .flatMap(Mono::error));
    }

    /**
     * Construye una excepción estándar para respuestas HTTP sin cuerpo.
     *
     * @param clientResponse Respuesta HTTP.
     * @return StandardRestException con información de error.
     */
    private StandardRestException buildStandardRestException(ClientResponse clientResponse) {
        return new StandardRestException(
                clientResponse.statusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}