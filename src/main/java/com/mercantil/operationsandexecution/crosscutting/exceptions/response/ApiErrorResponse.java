package com.mercantil.operationsandexecution.crosscutting.exceptions.response;


import com.mercantil.operationsandexecution.crosscutting.utility.TimeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Standardized error response envelope for the API.
 * <p>
 * Encapsulates status code, status string, message, timestamp, transaction id,
 * and a nested {@link ErrorData} section with detailed error information.
 *
 * @since 1.0
 */
@Getter
@Setter
@Schema(description = "Standard error response structure.")
public class ApiErrorResponse {

    @Schema(description = "HTTP status code as string", example = "400")
    private String statusCode;
    @Schema(description = "Status of the response", example = "ERROR")
    private String status;
    @Schema(description = "Human-readable error message", example = "The value message is mandatory")
    private String message;
    @Schema(description = "Wrapper for error details.")
    private ErrorData data;
    @Schema(description = "Timestamp of the error in ISO format", example = "2025-05-17T12:34:56")
    private String timestamp;
    @Schema(description = "Transaction ID for traceability", example = "f9d3-789f-123f-888f")
    private String transactionId;

    /**
     * Constructs an error response with details.
     *
     * @param statusCode    HTTP status code as string
     * @param message       resolved human-readable message
     * @param transactionId transaction identifier
     * @param errorDetails  detailed error information
     */
    public ApiErrorResponse(String statusCode, String message, String transactionId, ErrorDetails errorDetails) {
        this.statusCode = statusCode;
        this.status = "ERROR";
        this.message = message;
        this.timestamp = TimeHandler.currentDate();
        this.transactionId = transactionId;
        this.data = new ErrorData(errorDetails);
    }

    /**
     * Wrapper object for the error details section.
     */
    @Getter
    @Setter
    @Schema(description = "Wrapper object for the error details section.")
    public static class ErrorData {
        @Schema(description = "Error detail information.")
        private ErrorDetails errorDetails;

        /**
         * Creates a wrapper for {@link com.example.demo.exceptions.response.ApiErrorResponse.ErrorDetails}.
         *
         * @param errorDetails detailed error info
         */
        public ErrorData(ErrorDetails errorDetails) {
            this.errorDetails = errorDetails;
        }
    }

    /**
     * Detailed error information, including an internal code and a map of field messages.
     */
    @Getter
    @Setter
    @Schema(description = "Detailed error information.")
    public static class ErrorDetails {
        @Schema(description = "Internal error code identifier", example = "EXCEPTION_ERROR")
        private String code;
        @Schema(description = "Map of fields with related error messages.")
        private Map<String, String> fields;

        /**
         * Creates a new {@link com.example.demo.exceptions.response.ApiErrorResponse.ErrorDetails}.
         *
         * @param code   internal error code
         * @param fields field-to-message mapping
         */
        public ErrorDetails(String code, Map<String, String> fields) {
            this.code = code;
            this.fields = fields;
        }
    }
}
