package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.entrypoints.rest.validationhandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.Set;

import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.BODY_REQUIRED_MESSAGE;

/**
 * RequestAndHeaderValidationHandler es una clase base para manejar y validar peticiones HTTP en un contexto reactivo WebFlux.
 * <p>
 * Las subclases deben implementar {@link #processBody(ValidatedRequest, ServerRequest)} para definir la lógica de negocio.
 *
 * @param <T> Tipo del body de la petición a validar y procesar.
 * @param <M> Tipo del objeto de headers a extraer y validar (por ejemplo, metadata). Puede ser Void si no se usa.
 */
public abstract class RequestAndHeaderValidationHandler<T, M> {

    /**
     * Clase del body de la petición para deserialización y validación.
     */
    private final Class<T> validationClass;
    /**
     * Validator de Jakarta para validar body y headers.
     */
    private final Validator validator;

    /**
     * Constructor de RequestAndHeaderValidationHandler.
     *
     * @param clazz     Clase del body de la petición.
     * @param validator Instancia de Jakarta Validator.
     */
    protected RequestAndHeaderValidationHandler(Class<T> clazz, Validator validator) {
        this.validationClass = clazz;
        this.validator = validator;
    }

    /**
     * Maneja una petición con body: deserializa, valida body y headers, y procesa la petición.
     *
     * @param request Petición entrante.
     * @return Mono con la respuesta del servidor.
     */
    public final Mono<ServerResponse> handleRequest(final ServerRequest request) {
        return request.bodyToMono(this.validationClass)
                .switchIfEmpty(Mono.error(new ServerWebInputException(BODY_REQUIRED_MESSAGE)))
                .flatMap(body -> validateHeadersAndCreateRequest(body, request))
                .handle(this::validateBodyAndSink)
                .flatMap(validatedReq -> processBody(validatedReq, request));
    }

    /**
     * Valida los headers (si existen) y crea un objeto ValidatedRequest.
     *
     * @param body    Body deserializado de la petición (puede ser null).
     * @param request Petición entrante.
     * @return Mono con ValidatedRequest si la validación es exitosa, o error si falla.
     */
    private Mono<ValidatedRequest<T, M>> validateHeadersAndCreateRequest(T body, ServerRequest request) {
        M headerObject = extractHeaders(body, request);

        if (ObjectUtils.isNotEmpty(headerObject)) {
            Set<ConstraintViolation<M>> violations = validator.validate(headerObject);
            if (ObjectUtils.isNotEmpty(violations)) {
                return Mono.error(new ConstraintViolationException(violations));
            }
        }

        return Mono.just(ValidatedRequest.of(body, headerObject));
    }

    /**
     * Valida el body de la petición y emite el ValidatedRequest si es válido, o error si no lo es.
     *
     * @param validatedReq ValidatedRequest con body y headers.
     * @param sink         SynchronousSink para emitir el resultado o error.
     */
    private void validateBodyAndSink(ValidatedRequest<T, M> validatedReq, SynchronousSink<ValidatedRequest<T, M>> sink) {
        Set<ConstraintViolation<T>> violations = validator.validate(validatedReq.getBody());
        if (violations.isEmpty()) {
            sink.next(validatedReq);
        } else {
            sink.error(new ConstraintViolationException(violations));
        }
    }

    /**
     * Extrae y transforma los headers de la petición HTTP en un objeto de tipo M.
     * <p>
     * Este método debe ser implementado por las subclases para mapear los headers relevantes
     * del {@link ServerRequest} a un objeto de dominio (por ejemplo, Metadata).
     * Si no se requiere extraer headers, puede retornar null.
     *
     * @param body    El body deserializado de la petición.
     * @param request La petición HTTP que contiene los headers.
     * @return Objeto de tipo M construido a partir de los headers, o null si no corresponde.
     */
    protected abstract M extractHeaders(T body, ServerRequest request);

    /**
     * Método abstracto para procesar la petición validada. Debe ser implementado por las subclases.
     *
     * @param validatedRequest ValidatedRequest con body y headers validados.
     * @param originalRequest  Petición original.
     * @return Mono con la respuesta del servidor.
     */
    protected abstract Mono<ServerResponse> processBody(
            ValidatedRequest<T, M> validatedRequest, ServerRequest originalRequest);
}

