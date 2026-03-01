package com.mercantil.operationsandexecution.crosscutting.exceptions.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.EXTERNAL_EXCEPTION;


/**
 * Wrapper for HTTP client/server exceptions produced by external REST calls.
 * <p>
 * Carries the remote status code and the response body for further inspection
 * and mapping at the exception handler layer.
 *
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StandardRestException extends RuntimeException {

    /**
     * HTTP status code received from the external system.
     */
    private final HttpStatusCode statusCode;
    /**
     * Raw response body (transient to avoid accidental serialization).
     */
    private final transient Object errorResponseBody;

    /**
     * Creates a new {@link StandardRestException}.
     *
     * @param statusCode        remote status code
     * @param errorResponseBody remote response body (may be null or any type)
     * @param <T>               type of the response body
     */
    public <T> StandardRestException(HttpStatusCode statusCode, T errorResponseBody) {
        super(EXTERNAL_EXCEPTION);
        this.statusCode = statusCode;
        this.errorResponseBody = errorResponseBody;
    }

    /**
     * Converts {@link #statusCode} to a concrete {@link org.springframework.http.HttpStatus}.
     *
     * @return {@link org.springframework.http.HttpStatus} matching {@link #statusCode}
     * @throws IllegalArgumentException if the code cannot be mapped
     */
    public HttpStatus getStatusCodeAsHttpStatus() {
        return HttpStatus.valueOf(statusCode.value());
    }
}
