package com.mercantil.operationsandexecution.crosscutting.exceptions.handler.helper;

import com.mercantil.operationsandexecution.crosscutting.entrypointfilter.ConstEntryPointRest;
import com.mercantil.operationsandexecution.crosscutting.logging.ILoggerService;
import com.mercantil.operationsandexecution.crosscutting.messages.implementation.IMessageService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeoutException;

import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL;
import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.NOT_FOUND_EXCEPTION_CONSTANT_KEY_INTERNAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionResponseBuilderTest implements WithAssertions {

    private final String TX_ID = "TX-999";
    @Mock
    private IMessageService messageService;
    @Mock
    private ILoggerService loggerService;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private ServerHttpRequest request;
    private ExceptionResponseBuilder responseBuilder;

    @BeforeEach
    void setUp() {
        responseBuilder = new ExceptionResponseBuilder(messageService, loggerService);

        var headers = new HttpHeaders();
        headers.add(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID, TX_ID);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);

        when(loggerService.logError(any(), any(), any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("5. buildFromGenericException")
    void buildFromGenericException_Test() {
        Exception ex = new RuntimeException("Fatal");
        when(messageService.getHttpStatusCodeMessage("500")).thenReturn(Mono.just("Internal Error"));

        StepVerifier.create(responseBuilder.buildFromGenericException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    @DisplayName("6. buildFromTimeoutException")
    void buildFromTimeoutException_Test() {
        TimeoutException ex = new TimeoutException();
        when(messageService.getHttpStatusCodeMessage("504")).thenReturn(Mono.just("Timeout"));

        StepVerifier.create(responseBuilder.buildFromTimeoutException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.GATEWAY_TIMEOUT))
                .verifyComplete();
    }

    @Test
    @DisplayName("7. buildFromNoResourceFoundException")
    void buildFromNoResourceFoundException_Test() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        when(ex.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        ProblemDetail problemDetail = mock(ProblemDetail.class);
        when(problemDetail.getDetail()).thenReturn("not.found.error");
        when(ex.getBody()).thenReturn(problemDetail);

        when(messageService.getHttpStatusCodeMessage("404")).thenReturn(Mono.just("Not Found"));
        when(messageService.getMessage(NOT_FOUND_EXCEPTION_CONSTANT_KEY_INTERNAL)).thenReturn(Mono.just("Prefix"));

        StepVerifier.create(responseBuilder.buildFromNoResourceFoundException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND))
                .verifyComplete();
    }


    @Test
    @DisplayName("11. buildFromMethodArgumentTypeMismatchException")
    void buildFromMethodArgumentTypeMismatch_Test() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("paramName");
        when(messageService.getHttpStatusCodeMessage("400")).thenReturn(Mono.just("Bad Request"));
        when(messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL)).thenReturn(Mono.just("Prefix"));

        StepVerifier.create(responseBuilder.buildFromMethodArgumentTypeMismatchException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }
}