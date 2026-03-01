package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.controller.globalhandler;

import com.mercantil.operationsandexecution.crosscutting.exceptions.errors.ApiException;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.controller.globalhandler.helper.ExceptionResponse;
import com.mongodb.MongoException;
import io.lettuce.core.RedisException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@RequiredArgsConstructor
public class InfrastructureExceptionHandler {

    private final ExceptionResponse responseBuilder;

    @ExceptionHandler(ApiException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleApiException(ApiException exception, ServerWebExchange exchange) {
        return responseBuilder.buildFromApiException(exception, exchange);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleWebExchangeBindException(WebExchangeBindException exception, ServerWebExchange exchange) {
        return responseBuilder.buildFromValidationException(exception, exchange);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleConstraintViolationException(ConstraintViolationException exception, ServerWebExchange exchange) {
        return responseBuilder.buildFromConstraintViolationException(exception, exchange);
    }

    @ExceptionHandler(R2dbcException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleR2dbcException(R2dbcException ex, ServerWebExchange exchange) {
        return responseBuilder.buildFromDatabaseException(ex, exchange);
    }

    @ExceptionHandler(MongoException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleMongoException(MongoException ex, ServerWebExchange exchange) {
        return responseBuilder.buildFromDatabaseException(ex, exchange);
    }

    @ExceptionHandler(RedisException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleRedisException(RedisException ex, ServerWebExchange exchange) {
        return responseBuilder.buildFromDatabaseException(ex, exchange);
    }

}
