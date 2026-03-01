package com.mercantil.operationsandexecution.crosscutting.exceptions.utility;


import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiResponseDto;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Response factory to build success and error envelopes in a consistent way.
 *
 * @since 1.0
 */
@UtilityClass
public class ResponseFactory {

    /**
     * Creates a success response envelope.
     *
     * @param status        HTTP status
     * @param message       human-readable message
     * @param data          payload to include (nullable)
     * @param transactionId transaction identifier
     * @param <T>           payload type
     * @return a populated {@link com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiResponseDto}
     */
    public static <T> ApiResponseDto<T> createSuccessResponse(HttpStatus status, String message, T data,
                                                              String transactionId) {
        return new ApiResponseDto<>(String.valueOf(status.value()), message, data, transactionId);
    }

    /**
     * Creates an error response envelope and wraps it in {@link org.springframework.http.ResponseEntity}.
     *
     * @param status        HTTP status
     * @param message       error message
     * @param transactionId transaction identifier
     * @param details       detailed error section
     * @return response entity with the error body and status
     */
    public static ResponseEntity<ApiErrorResponse> createErrorResponse(HttpStatus status,
                                                                       String message, String transactionId, ApiErrorResponse.ErrorDetails details) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                String.valueOf(status.value()),
                message,
                transactionId,
                details);
        return new ResponseEntity<>(errorResponse, status);
    }
}
