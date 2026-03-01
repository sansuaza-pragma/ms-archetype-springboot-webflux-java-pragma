package com.mercantil.operationsandexecution.crosscutting.exceptions.response;

import com.mercantil.operationsandexecution.crosscutting.utility.TimeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic success response envelope for the API.
 * <p>
 * Contains status code, status string, message, timestamp, transaction id, and
 * a generic data payload.
 *
 * @param <T> payload type
 * @since 1.0
 */
@Getter
@Setter
@Schema(description = "Generic structure for successful API responses.")
public class ApiResponseDto<T> {
    @Schema(description = "HTTP status code as string", example = "200")
    private String statusCode;
    @Schema(description = "Status indicator of the response", example = "SUCCESS")
    private String status;
    @Schema(description = "Response message", example = "OK - The request was successfully executed.")
    private String message;
    @Schema(description = "Information returned according to the objectype flow")
    private T data;
    @Schema(description = "Timestamp of the response", example = "2025-05-17T15:03:00")
    private String timestamp;
    @Schema(description = "Transaction identifier for traceability", example = "abcd1234")
    private String transactionId;

    /**
     * Constructs a success response with payload.
     *
     * @param statusCode    HTTP status code as string
     * @param message       human-readable message
     * @param data          payload to return (maybe null)
     * @param transactionId transaction identifier
     */
    public ApiResponseDto(String statusCode, String message, T data, String transactionId) {
        this.statusCode = statusCode;
        this.status = "SUCCESS";
        this.message = message;
        this.data = data;
        this.timestamp = TimeHandler.currentDate();
        this.transactionId = transactionId;
    }
}
