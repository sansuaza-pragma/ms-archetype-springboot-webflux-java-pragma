package com.mercantil.operationsandexecution.crosscutting.documentation.response;

import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for loan operations, based on the generic wrapper
 * {@link com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiResponseDto}. Keeps a flexible type for the {@code data} field.
 *
 * @since 1.0
 */
@Schema(description = "Contains the generic management response to the loan")
public class LoanResponseDto extends ApiResponseDto<Object> {

    /**
     * Creates a new generic loan response.
     *
     * @param statusCode    outcome status code (e.g., HTTP as string or internal code).
     * @param message       human-readable message describing the result.
     * @param data          payload returned by the operation (nullable).
     * @param transactionId transaction identifier for end-to-end traceability.
     */
    public LoanResponseDto(String statusCode, String message, Object data, String transactionId) {
        super(statusCode, message, data, transactionId);
    }
}
