package com.mercantil.operationsandexecution.crosscutting.exceptions.utility;

import com.mercantil.operationsandexecution.crosscutting.exceptions.errors.ApiException;
import lombok.experimental.UtilityClass;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Objects;

import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.UTILITY_EXCEPTION_MESSAGE;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.UTILITY_EXCEPTION_TYPE;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.UTILITY_KEY_NAME_EXCEPTION_MESSAGE;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.UTILITY_KEY_NAME_EXCEPTION_TYPE;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.UTILITY_MESSAGE_URL;


/**
 * Utility mapper that converts different exception types into a normalized
 * {@code Map<String, String>} structure suitable for inclusion in
 * {@link com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse.ErrorDetails}.
 *
 * <p>Each mapper method focuses on a specific exception and produces keys that are
 * consistent across the API error payload.</p>
 *
 * @since 1.0
 */
@UtilityClass
public class ExceptionFieldMapper {

    /**
     * Maps a domain {@link com.mercantil.operationsandexecution.crosscutting.exceptions.errors.ApiException} to a field map.
     *
     * @param exception the domain exception
     * @param message   resolved, human-readable message
     * @return a normalized map with type and message
     */
    public static Map<String, String> fromApiException(ApiException exception, String message) {
        return Map.of(
                UTILITY_EXCEPTION_TYPE, exception.getClass().getSimpleName(),
                UTILITY_EXCEPTION_MESSAGE, message
        );
    }

    public static Map<String, String> fromValidationException(WebExchangeBindException exception, String prefixExceptionMessage) {
        Map<String, String> fields = new java.util.HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            fields.put(UTILITY_KEY_NAME_EXCEPTION_TYPE, error.getClass().getSimpleName());
            fields.put(UTILITY_KEY_NAME_EXCEPTION_MESSAGE,
                    prefixExceptionMessage.concat(Objects.requireNonNull(error.getDefaultMessage())));
        });
        return fields;
    }

    public static Map<String, String> fromConstraintViolationException(String exceptionType, String exceptionMessage,
                                                                       String prefixExceptionMessage) {
        return Map.of(
                UTILITY_KEY_NAME_EXCEPTION_TYPE, exceptionType,
                UTILITY_KEY_NAME_EXCEPTION_MESSAGE, prefixExceptionMessage.concat(exceptionMessage)
        );
    }

    public static Map<String, String> fromGenericException(Exception exception, String message) {
        return Map.of(
                UTILITY_EXCEPTION_TYPE, exception.getClass().getSimpleName(),
                UTILITY_EXCEPTION_MESSAGE, message
        );
    }

    public static Map<String, String> fromNoResourceFoundException(NoResourceFoundException exception, String prefixExceptionMessage) {
        return Map.of(
                UTILITY_KEY_NAME_EXCEPTION_TYPE, exception.getClass().getSimpleName(),
                UTILITY_KEY_NAME_EXCEPTION_MESSAGE, prefixExceptionMessage.concat(UTILITY_MESSAGE_URL)
        );
    }

    public static Map<String, String> fromMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, String prefixExceptionMessage) {
        return Map.of(
                UTILITY_KEY_NAME_EXCEPTION_TYPE, exception.getClass().getSimpleName(),
                UTILITY_KEY_NAME_EXCEPTION_MESSAGE, prefixExceptionMessage.concat(exception.getName())
        );
    }

}

