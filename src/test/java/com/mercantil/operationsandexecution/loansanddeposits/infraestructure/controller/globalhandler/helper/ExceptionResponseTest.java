package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.controller.globalhandler.helper;

import com.mercantil.operationsandexecution.crosscutting.exceptions.errors.ApiException;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.controller.globalhandler.InfrastructureExceptionHandler;
import com.mongodb.MongoException;
import io.lettuce.core.RedisException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionResponseTest implements WithAssertions {

    @Mock
    private ExceptionResponse responseBuilder;

    @Mock
    private ServerWebExchange exchange;

    private InfrastructureExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new InfrastructureExceptionHandler(responseBuilder);
    }

    @Test
    @DisplayName("Should delegate ApiException handling to responseBuilder")
    void handleApiException_ShouldDelegate() {
        // Given
        ApiException exception = new ApiException(HttpStatus.BAD_REQUEST, "Business Error", "ERR-001", EnumActionLogs.GENERIC_ERROR, null);
        ResponseEntity<ApiErrorResponse> expectedResponse = ResponseEntity.badRequest().build();

        when(responseBuilder.buildFromApiException(eq(exception), eq(exchange)))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<ResponseEntity<ApiErrorResponse>> result = exceptionHandler.handleApiException(exception, exchange);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }


    @Test
    @DisplayName("Should delegate Database exceptions (Mongo) to responseBuilder database method")
    void handleMongoException_ShouldDelegateToDatabaseBuild() {
        // Given
        MongoException exception = new MongoException("DB Error");
        ResponseEntity<ApiErrorResponse> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(responseBuilder.buildFromDatabaseException(eq(exception), eq(exchange)))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<ResponseEntity<ApiErrorResponse>> result = exceptionHandler.handleMongoException(exception, exchange);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle WebExchangeBindException")
    void handleWebExchangeBindException_Success() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.badRequest().build();

        when(responseBuilder.buildFromValidationException(eq(ex), eq(exchange)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(exceptionHandler.handleWebExchangeBindException(ex, exchange))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void handleConstraintViolationException_Success() {
        ConstraintViolationException ex = new ConstraintViolationException(new HashSet<>());
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.badRequest().build();

        when(responseBuilder.buildFromConstraintViolationException(eq(ex), eq(exchange)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(exceptionHandler.handleConstraintViolationException(ex, exchange))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle R2dbcException (Database)")
    void handleR2dbcException_Success() {
        R2dbcException ex = mock(R2dbcException.class);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.internalServerError().build();

        when(responseBuilder.buildFromDatabaseException(eq(ex), eq(exchange)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(exceptionHandler.handleR2dbcException(ex, exchange))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle RedisException (Cache)")
    void handleRedisException_Success() {
        RedisException ex = mock(RedisException.class);
        ResponseEntity<ApiErrorResponse> response = ResponseEntity.internalServerError().build();

        when(responseBuilder.buildFromDatabaseException(eq(ex), eq(exchange)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(exceptionHandler.handleRedisException(ex, exchange))
                .expectNext(response)
                .verifyComplete();
    }

}