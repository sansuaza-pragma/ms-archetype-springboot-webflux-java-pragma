package com.mercantil.operationsandexecution.crosscutting.exceptions.errors;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Domain-level runtime exception representing a controlled API error.
 * <p>
 * Wraps an HTTP status, a message key (to be resolved by the message service),
 * a transaction id for traceability, a log action, and optional additional data.
 *
 * @since 1.0
 */
@Getter
public class ApiException extends RuntimeException {

    /**
     * HTTP status to be returned to the client.
     */
    private final HttpStatus httpStatus;
    /**
     * String representation of the status code (e.g., {@code "400 BAD_REQUEST"}).
     */
    private final String errorCode;
    /**
     * Resolvable i18n key or message identifier.
     */
    private final String messageKey;
    /**
     * Transaction identifier for end-to-end tracing.
     */
    private final String transactionId;
    /**
     * Log action code to categorize the error in telemetry.
     */
    private final EnumActionLogs action;
    /**
     * Optional extra context not meant for serialization.
     */
    private final transient Object additionalData;

    /**
     * Creates a new {@link ApiException}.
     *
     * @param httpStatus     HTTP status to return
     * @param messageKey     message/i18n key describing the error
     * @param transactionId  transaction identifier for traceability
     * @param action         log action to register
     * @param additionalData optional extra data for internal use
     */
    public ApiException(HttpStatus httpStatus, String messageKey,
                        String transactionId, EnumActionLogs action, Object additionalData) {
        super(messageKey);
        this.httpStatus = httpStatus;
        this.errorCode = httpStatus.toString();
        this.messageKey = messageKey;
        this.transactionId = transactionId;
        this.action = action;
        this.additionalData = additionalData;
    }
}
