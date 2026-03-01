package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.entrypoints.rest.validationhandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Handler base para la validación de headers en peticiones reactivas.
 * <p>
 * Esta clase abstracta permite extraer y validar los headers de un {@link ServerRequest}
 * usando un objeto de tipo genérico <M> y un {@link Validator} de Jakarta Validation.
 * Si la validación falla, retorna un error reactivo con {@link ConstraintViolationException}.
 * <p>
 * Las subclases deben implementar cómo extraer los headers y cómo procesar la petición.
 *
 * @param <M> Tipo del objeto de metadata extraído y validado desde los headers.
 */
public abstract class HeaderValidationHandler<M> {

    /**
     * Validator de Jakarta para validar el objeto de headers.
     */
    private final Validator validator;

    /**
     * Constructor.
     * @param validator instancia de {@link Validator} para validación de headers.
     */
    protected HeaderValidationHandler(Validator validator) {
        this.validator = validator;
    }

    /**
     * Maneja la petición extrayendo y validando los headers, y luego procesando la lógica de negocio.
     *
     * @param request petición reactiva entrante.
     * @return Mono con la respuesta del servidor.
     */
    public final Mono<ServerResponse> handleRequest(final ServerRequest request) {
        return extractAndValidateHeaders(request)
                .flatMap(metadata -> processRequest(request, metadata));
    }

    /**
     * Extrae y valida los headers del request. Si hay violaciones, retorna un error reactivo.
     *
     * @param request petición reactiva entrante.
     * @return Mono con el objeto de metadata validado o error si hay violaciones.
     */
    private Mono<M> extractAndValidateHeaders(ServerRequest request) {
        M metadata = extractHeaders(request);
        Set<ConstraintViolation<M>> violations = validator.validate(metadata);
        return !violations.isEmpty() ? Mono.error(new ConstraintViolationException(violations)) : Mono.just(metadata);
    }

    /**
     * Extrae los headers del request y los convierte en un objeto de tipo <M>.
     *
     * @param request petición reactiva entrante.
     * @return objeto de metadata construido a partir de los headers.
     */
    protected abstract M extractHeaders(ServerRequest request);

    /**
     * Procesa la petición usando el objeto de metadata validado.
     *
     * @param request petición reactiva entrante.
     * @param metadata objeto de metadata validado.
     * @return Mono con la respuesta del servidor.
     */
    protected abstract Mono<ServerResponse> processRequest(ServerRequest request, M metadata);
}