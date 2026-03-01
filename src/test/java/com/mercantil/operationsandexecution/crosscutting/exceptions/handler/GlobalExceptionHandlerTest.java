package com.mercantil.operationsandexecution.crosscutting.exceptions.handler;

import com.mercantil.operationsandexecution.crosscutting.exceptions.handler.helper.ExceptionResponseBuilder;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest implements WithAssertions {

    @Mock
    private ExceptionResponseBuilder responseBuilder;

    @Mock
    private ServerWebExchange exchange;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler(responseBuilder);
    }


    @Test
    @DisplayName("Should delegate TimeoutException to responseBuilder")
    void handleTimeoutException_ShouldDelegate() {
        // Given
        TimeoutException exception = new TimeoutException("Connection timed out");
        ResponseEntity<ApiErrorResponse> expectedResponse = ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();

        when(responseBuilder.buildFromTimeoutException(eq(exception), eq(exchange)))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<ResponseEntity<ApiErrorResponse>> result = globalExceptionHandler.handleTimeoutException(exception, exchange);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }


    @Test
    @DisplayName("Should handle generic Exception via buildFromGenericException")
    void handleGenericException_ShouldDelegate() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");
        ResponseEntity<ApiErrorResponse> expectedResponse = ResponseEntity.internalServerError().build();

        when(responseBuilder.buildFromGenericException(eq(exception), eq(exchange)))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<ResponseEntity<ApiErrorResponse>> result = globalExceptionHandler.handleGenericException(exception, exchange);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }


    @Test
    @DisplayName("Should handle ServerWebInputException")
    void handleServerWebInputException_Success() {
        ServerWebInputException ex = mock(ServerWebInputException.class);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.badRequest().build();

        when(responseBuilder.buildFromGenericException(eq(ex), eq(exchange)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(globalExceptionHandler.handleServerWebInputException(ex, exchange))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle NoResourceFoundException")
    void handleNoResourceFoundException_Success() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.status(404).build();

        when(responseBuilder.buildFromNoResourceFoundException(eq(ex), eq(exchange)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(globalExceptionHandler.handleNoResourceFoundException(ex, exchange))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle MethodArgumentTypeMismatchException")
    void handleMethodArgumentTypeMismatchException_Success() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.badRequest().build();

        when(responseBuilder.buildFromMethodArgumentTypeMismatchException(eq(ex), eq(exchange)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex, exchange))
                .expectNext(response)
                .verifyComplete();
    }

}