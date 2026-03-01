package com.mercantil.operationsandexecution.crosscutting.exceptions.handler;


import com.mercantil.operationsandexecution.crosscutting.exceptions.handler.helper.ExceptionResponseBuilder;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionResponseBuilder responseBuilder;

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleServerWebInputException(ServerWebInputException exception, ServerWebExchange exchange) {
        return responseBuilder.buildFromGenericException(exception, exchange);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleNoResourceFoundException(NoResourceFoundException exception, ServerWebExchange exchange) {
        return responseBuilder.buildFromNoResourceFoundException(exception, exchange);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, ServerWebExchange exchange) {
        return responseBuilder.buildFromMethodArgumentTypeMismatchException(exception, exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleGenericException(Exception exception, ServerWebExchange exchange) {
        return responseBuilder.buildFromGenericException(exception, exchange);
    }

    @ExceptionHandler(TimeoutException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleTimeoutException(TimeoutException ex, ServerWebExchange exchange) {
        return responseBuilder.buildFromTimeoutException(ex, exchange);
    }

}