package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.controller.globalhandler;

import com.mercantil.operationsandexecution.crosscutting.entrypointfilter.ConstEntryPointRest;
import com.mercantil.operationsandexecution.crosscutting.exceptions.errors.ApiException;
import com.mercantil.operationsandexecution.crosscutting.logging.ILoggerService;
import com.mercantil.operationsandexecution.crosscutting.messages.implementation.IMessageService;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.controller.globalhandler.helper.ExceptionResponse;
import com.mongodb.MongoException;
import io.lettuce.core.RedisException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static com.mercantil.operationsandexecution.crosscutting.exceptions.constants.ExceptionsConstants.BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfrastructureExceptionHandlerTest implements WithAssertions {

    private final String TX_ID = "TX-999";
    @Mock
    private IMessageService messageService;
    @Mock
    private ILoggerService loggerService;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private ServerHttpRequest request;
    private ExceptionResponse responseBuilder;

    @BeforeEach
    void setUp() {
        responseBuilder = new ExceptionResponse(messageService, loggerService);

        var headers = new HttpHeaders();
        headers.add(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID, TX_ID);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);

        when(loggerService.logError(any(), any(), any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("1. buildFromApiException - Bad Request Case (400)")
    void buildFromApiException_Status400() {
        ApiException ex = new ApiException(HttpStatus.BAD_REQUEST, "Invalid field", "dummy.key", EnumActionLogs.GENERIC_ERROR, null);
        when(messageService.getHttpStatusCodeMessage("400")).thenReturn(Mono.just("Bad Request"));
        when(messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL)).thenReturn(Mono.just("Prefix: "));

        StepVerifier.create(responseBuilder.buildFromApiException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    @DisplayName("2. buildFromApiException - Generic Case (Not 400)")
    void buildFromApiException_OtherStatus() {
        ApiException ex = new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", "dummy.key", EnumActionLogs.GENERIC_ERROR, null);
        when(messageService.getHttpStatusCodeMessage("500")).thenReturn(Mono.just("Internal Error"));
        when(messageService.getMessage(anyString())).thenReturn(Mono.just("Localized Error"));
        StepVerifier.create(responseBuilder.buildFromApiException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    @DisplayName("3. buildFromValidationException")
    void buildFromValidationException_Test() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        when(ex.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        ProblemDetail problemDetail = mock(ProblemDetail.class);
        when(problemDetail.getDetail()).thenReturn("validation.error");
        when(ex.getBody()).thenReturn(problemDetail);

        BindingResult bindingResult = mock(BindingResult.class);
        ObjectError objectError = mock(ObjectError.class);
        when(objectError.getDefaultMessage()).thenReturn("validation.error");
        when(bindingResult.getAllErrors()).thenReturn(List.of(objectError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        when(messageService.getHttpStatusCodeMessage("400")).thenReturn(Mono.just("Bad Request"));
        when(messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL)).thenReturn(Mono.just("Prefix"));
        when(messageService.getMessage("validation.error")).thenReturn(Mono.just("Mensaje de validación"));

        StepVerifier.create(responseBuilder.buildFromValidationException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    @DisplayName("4. buildFromConstraintViolationException")
    void buildFromConstraintViolationException_Test() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("violation.key");
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));

        when(messageService.getMessage("violation.key")).thenReturn(Mono.just("Invalid Constraint"));
        when(messageService.getHttpStatusCodeMessage("400")).thenReturn(Mono.just("Bad Request"));
        when(messageService.getMessage(BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL)).thenReturn(Mono.just("Prefix"));

        StepVerifier.create(responseBuilder.buildFromConstraintViolationException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    @DisplayName("8. buildFromDatabaseException - R2DBC")
    void buildFromDatabaseException_R2dbc() {
        R2dbcException ex = mock(R2dbcException.class);
        when(ex.getErrorCode()).thenReturn(999);
        when(ex.getMessage()).thenReturn("DB error");
        when(messageService.getHttpStatusCodeMessage("999")).thenReturn(Mono.just("DB Error"));

        StepVerifier.create(responseBuilder.buildFromDatabaseException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    @DisplayName("9. buildFromDatabaseException - Mongo")
    void buildFromDatabaseException_Mongo() {
        MongoException ex = new MongoException(555, "Mongo Error");
        when(messageService.getHttpStatusCodeMessage("555")).thenReturn(Mono.just("Mongo Fail"));

        StepVerifier.create(responseBuilder.buildFromDatabaseException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    @DisplayName("10. buildFromDatabaseException - Redis")
    void buildFromDatabaseException_Redis() {
        RedisException ex = new RedisException("Redis Fail");
        when(messageService.getHttpStatusCodeMessage("500")).thenReturn(Mono.just("Cache Error"));

        StepVerifier.create(responseBuilder.buildFromDatabaseException(ex, exchange))
                .assertNext(res -> assertThat(res.getStatusCode())
                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

}