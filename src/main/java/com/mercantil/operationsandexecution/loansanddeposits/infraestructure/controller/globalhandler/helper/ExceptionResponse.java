package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.controller.globalhandler.helper;

import com.mercantil.operationsandexecution.crosscutting.entrypointfilter.ConstEntryPointRest;
import com.mercantil.operationsandexecution.crosscutting.exceptions.errors.ApiException;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import com.mercantil.operationsandexecution.crosscutting.exceptions.utility.ExceptionFieldMapper;
import com.mercantil.operationsandexecution.crosscutting.exceptions.utility.ExceptionResponseGeneric;
import com.mercantil.operationsandexecution.crosscutting.logging.ILoggerService;
import com.mercantil.operationsandexecution.crosscutting.messages.implementation.IMessageService;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import com.mongodb.MongoException;
import io.lettuce.core.RedisException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.EXCEPTION_HANDLER_ERROR;
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
public class ExceptionResponse {

    private final IMessageService messageService;
    private final ILoggerService loggerService;

    /**
     * Builds a response from a domain {@link com.mercantil.operationsandexecution.crosscutting.exceptions.errors.ApiException}.
     *
     * @param exception domain exception
     * @param ctx       web request context
     * @return a standardized error response
     */
    public Mono<ResponseEntity<ApiErrorResponse>> buildFromApiException(ApiException exception, ServerWebExchange ctx) {
        var statusCodeMessage = getHttpStatusCodeMessage(String.valueOf(exception.getHttpStatus().value()));
        var messageInternal = resolveErrorMessage(exception, statusCodeMessage);
        var fields = messageInternal.map(msg -> ExceptionFieldMapper.fromApiException(exception, msg));
        return Mono.zip(statusCodeMessage, fields, messageInternal, getTransactionId(ctx))
                .flatMap(tuple ->
                        loggerService.logError(exception.getAction(), new Throwable(tuple.getT3()), exception.getAdditionalData())
                                .then(ExceptionResponseGeneric.createResponse(
                                        exception.getHttpStatus(),
                                        tuple.getT1(),
                                        tuple.getT4(),
                                        tuple.getT2()
                                ))
                );
    }

    private Mono<String> resolveErrorMessage(ApiException ex, Mono<String> statusCodeMessage) {
        return ex.getHttpStatus().value() == 400
                ? messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL).map(mss -> mss.concat(ex.getMessage()))
                : (StringUtils.isNotEmpty(ex.getMessageKey())
                ? messageService.getMessage(ex.getMessageKey())
                : statusCodeMessage);

    }


    public Mono<ResponseEntity<ApiErrorResponse>> buildFromValidationException(WebExchangeBindException exception,
                                                                               ServerWebExchange ctx) {

        var statusCodeMessage = getHttpStatusCodeMessage(String.valueOf(HttpStatus.BAD_REQUEST.value()));

        var message = exception.getBindingResult().getAllErrors().stream()
                .findAny()
                .map(v -> messageService.getMessage(v.getDefaultMessage()))
                .orElse(statusCodeMessage);


        var transactionId = getTransactionId(ctx);
        var prefixExceptionMessage = messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL);

        return Mono.zip(prefixExceptionMessage, transactionId, statusCodeMessage, message)
                .flatMap(mss ->
                        loggerService.logError(EnumActionLogs.EXCEPTION_METHOD_ARGUMENT_NOT_VALID_ERROR,
                                        new Throwable(StringUtils.defaultIfEmpty(exception.getBody().getDetail(),
                                                EXCEPTION_HANDLER_ERROR)), mss.getT4())
                                .then(
                                        ExceptionResponseGeneric.createResponse(
                                                HttpStatus.valueOf(exception.getStatusCode().value()),
                                                mss.getT3(),
                                                mss.getT2(),
                                                ExceptionFieldMapper.fromValidationException(exception, mss.getT1())
                                        )
                                )
                );
    }

    public Mono<ResponseEntity<ApiErrorResponse>> buildFromConstraintViolationException(ConstraintViolationException exception,
                                                                                        ServerWebExchange ctx) {

        var statusCodeMessage = getHttpStatusCodeMessage(String.valueOf(HttpStatus.BAD_REQUEST.value()));

        var message = exception.getConstraintViolations().stream()
                .findAny()
                .map(v -> messageService.getMessage(v.getMessage()))
                .orElse(statusCodeMessage);
        return message.flatMap(mss -> buildBadRequestResponse(mss, exception, ctx));
    }

    private Mono<ResponseEntity<ApiErrorResponse>> buildBadRequestResponse(
            String message,
            Exception exception,
            ServerWebExchange ctx) {

        var transactionId = getTransactionId(ctx);
        var statusCodeMessage = getHttpStatusCodeMessage(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        var prefixExceptionMessage = messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL);

        return Mono.zip(statusCodeMessage, transactionId, prefixExceptionMessage)
                .flatMap(mss ->
                        loggerService.logError(EnumActionLogs.EXCEPTION_METHOD_ARGUMENT_NOT_VALID_ERROR,
                                        new Throwable(EXCEPTION_HANDLER_ERROR), message)
                                .then(ExceptionResponseGeneric.createResponse(
                                        HttpStatus.BAD_REQUEST,
                                        mss.getT1(),
                                        mss.getT2(),
                                        ExceptionFieldMapper
                                                .fromConstraintViolationException(exception.getClass().getSimpleName(),
                                                        message,
                                                        mss.getT3()
                                                ))
                                ));
    }


    public Mono<ResponseEntity<ApiErrorResponse>> buildFromDatabaseException(MongoException exception, ServerWebExchange ctx) {
        var message = getHttpStatusCodeMessage(String.valueOf(exception.getCode()));
        var transactionId = getTransactionId(ctx);

        return Mono.zip(transactionId, message)
                .flatMap(mss ->
                        loggerService.logError(
                                EnumActionLogs.EXCEPTION_HTTP_CLIENT_ERROR,
                                new Throwable(exception.getMessage()),
                                exception.getCause()
                        ).then(
                                ExceptionResponseGeneric.createResponse(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        mss.getT2(),
                                        mss.getT1(),
                                        ExceptionFieldMapper.fromGenericException(exception, exception.getMessage())
                                )
                        )
                );
    }

    public Mono<ResponseEntity<ApiErrorResponse>> buildFromDatabaseException(RedisException exception, ServerWebExchange ctx) {
        var message = getHttpStatusCodeMessage(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        var transactionId = getTransactionId(ctx);

        return Mono.zip(transactionId, message)
                .flatMap(mss ->
                        loggerService.logError(
                                EnumActionLogs.EXCEPTION_HTTP_CLIENT_ERROR,
                                new Throwable(exception.getMessage()),
                                exception.getCause()
                        ).then(
                                ExceptionResponseGeneric.createResponse(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        mss.getT2(),
                                        mss.getT1(),
                                        ExceptionFieldMapper.fromGenericException(exception, exception.getMessage())
                                )
                        )
                );
    }

    public Mono<ResponseEntity<ApiErrorResponse>> buildFromDatabaseException(R2dbcException exception, ServerWebExchange ctx) {
        var message = getHttpStatusCodeMessage(String.valueOf(exception.getErrorCode()));
        var transactionId = getTransactionId(ctx);

        return Mono.zip(transactionId, message)
                .flatMap(mss ->
                        loggerService.logError(
                                EnumActionLogs.EXCEPTION_HTTP_CLIENT_ERROR,
                                new Throwable(exception.getMessage()),
                                exception.getSql()
                        ).then(
                                ExceptionResponseGeneric.createResponse(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        mss.getT2(),
                                        mss.getT1(),
                                        ExceptionFieldMapper.fromGenericException(exception, exception.getMessage())
                                )
                        )
                );
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

