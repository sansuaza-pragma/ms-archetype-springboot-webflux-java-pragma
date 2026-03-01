package com.mercantil.operationsandexecution.crosscutting.exceptions.handler.helper;

import com.mercantil.operationsandexecution.crosscutting.entrypointfilter.ConstEntryPointRest;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import com.mercantil.operationsandexecution.crosscutting.exceptions.utility.ExceptionFieldMapper;
import com.mercantil.operationsandexecution.crosscutting.exceptions.utility.ExceptionResponseGeneric;
import com.mercantil.operationsandexecution.crosscutting.logging.ILoggerService;
import com.mercantil.operationsandexecution.crosscutting.messages.implementation.IMessageService;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.EXCEPTION_HANDLER_ERROR;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.NOT_FOUND_EXCEPTION_CONSTANT_KEY_INTERNAL;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.TRANSACTION_ID_NOT_SPECIFIED;


/**
 * Helper component to consistently build {@link com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse} envelopes
 * for different exception types and to register error logs.
 * <p>
 * This class centralizes message resolution, field mapping and response entity creation.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class ExceptionResponseBuilder {

    private final IMessageService messageService;
    private final ILoggerService loggerService;

    public Mono<ResponseEntity<ApiErrorResponse>> buildFromGenericException(Exception exception, ServerWebExchange ctx) {
        var transactionId = getTransactionId(ctx);
        var message = getHttpStatusCodeMessage(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        return Mono.zip(transactionId, message).flatMap(mss ->
                loggerService.logError(EnumActionLogs.EXCEPTION_CAPTURED_GENERIC, exception, exception.getMessage())
                        .then(ExceptionResponseGeneric.createResponse(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        mss.getT2(),
                                        mss.getT1(),
                                        ExceptionFieldMapper.fromGenericException(exception, mss.getT2())
                                )
                        )
        );
    }

    public Mono<ResponseEntity<ApiErrorResponse>> buildFromTimeoutException(TimeoutException exception, ServerWebExchange ctx) {
        var transactionId = getTransactionId(ctx);
        var message = getHttpStatusCodeMessage(String.valueOf(HttpStatus.GATEWAY_TIMEOUT.value()));

        return Mono.zip(transactionId, message).flatMap(mss ->
                loggerService.logError(EnumActionLogs.EXCEPTION_HTTP_CLIENT_ERROR, exception, exception.getMessage())
                        .then(ExceptionResponseGeneric.createResponse(
                                        HttpStatus.GATEWAY_TIMEOUT,
                                        mss.getT2(),
                                        mss.getT1(),
                                        ExceptionFieldMapper.fromGenericException(exception, mss.getT2())
                                )
                        )
        );
    }

    public Mono<ResponseEntity<ApiErrorResponse>> buildFromNoResourceFoundException(NoResourceFoundException exception, ServerWebExchange ctx) {
        var message = getHttpStatusCodeMessage(String.valueOf(exception.getStatusCode().value()));
        var transactionId = getTransactionId(ctx);
        var prefixExceptionMessage = messageService.getMessage(NOT_FOUND_EXCEPTION_CONSTANT_KEY_INTERNAL);

        return Mono.zip(transactionId, message, prefixExceptionMessage).flatMap(mss ->
                loggerService.logError(EnumActionLogs.EXCEPTION_NO_RESOURCE_FOUND_EXCEPTION_ERROR,
                                new Throwable(EXCEPTION_HANDLER_ERROR), exception.getBody().getDetail())
                        .then(ExceptionResponseGeneric.createResponse(
                                        HttpStatus.valueOf(exception.getStatusCode().value()),
                                        mss.getT2(),
                                        mss.getT1(),
                                        ExceptionFieldMapper.fromNoResourceFoundException(exception, mss.getT3())
                                )
                        )
        );
    }

    public Mono<ResponseEntity<ApiErrorResponse>> buildFromMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, ServerWebExchange ctx) {

        var message = getHttpStatusCodeMessage(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        var transactionId = getTransactionId(ctx);
        var prefixExceptionMessage = messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL);

        return Mono.zip(transactionId, message, prefixExceptionMessage).flatMap(mss ->
                loggerService.logError(EnumActionLogs.EXCEPTION_METHOD_ARGUMENT_NOT_VALID_ERROR, new Throwable(EXCEPTION_HANDLER_ERROR),
                                exception.getMessage())
                        .then(ExceptionResponseGeneric.createResponse(
                                HttpStatus.BAD_REQUEST,
                                mss.getT2(),
                                mss.getT1(),
                                ExceptionFieldMapper.fromMethodArgumentTypeMismatchException(exception, mss.getT3())
                        )));
    }

    private Mono<String> getHttpStatusCodeMessage(String statusCode) {
        return messageService.getHttpStatusCodeMessage(statusCode);
    }

    private Mono<String> getTransactionId(ServerWebExchange exchange) {
        String id = exchange.getRequest().getHeaders()
                .getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID);
        return Mono.just(StringUtils.defaultIfEmpty(id, TRANSACTION_ID_NOT_SPECIFIED));
    }

}

