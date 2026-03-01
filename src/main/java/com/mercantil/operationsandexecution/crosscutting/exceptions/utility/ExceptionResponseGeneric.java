package com.mercantil.operationsandexecution.crosscutting.exceptions.utility;

import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Factory helpers to create standardized {@link com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse} envelopes
 * and wrap them in {@link org.springframework.http.ResponseEntity}.
 *
 * @since 1.0
 */
@UtilityClass
public class ExceptionResponseGeneric {

    /**
     * Creates a standardized error response with the provided fields map.
     *
     * @param status        HTTP status to return
     * @param message       resolved human-readable message
     * @param transactionId transaction identifier
     * @param fields        normalized field-to-message map
     * @return a {@link org.springframework.http.ResponseEntity} containing the  {@link com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse}
     */
    public static Mono<ResponseEntity<ApiErrorResponse>> createResponse(HttpStatus status, String message, String transactionId, Map<String, String> fields) {
        ApiErrorResponse.ErrorDetails details = new ApiErrorResponse.ErrorDetails(String.valueOf(status.value()), fields);
        return Mono.just(ResponseFactory.createErrorResponse(status, message, transactionId, details));
    }
}
