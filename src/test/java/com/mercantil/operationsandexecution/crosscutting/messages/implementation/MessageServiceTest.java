package com.mercantil.operationsandexecution.crosscutting.messages.implementation;

import com.mercantil.operationsandexecution.crosscutting.entrypointfilter.ConstEntryPointRest;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.Locale;

import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_GENERIC_ERROR_MESSAGE;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_HTTP_RESPONSE_LITERAL;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_HTTP_RESPONSE_MAP;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_TRANSACTION_ID_NOT_SPECIFIED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest implements WithAssertions {

    @Mock
    private MessageSource messageSource;

    @Mock
    private ServerWebExchange exchange;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageSource);
    }

    @Test
    @DisplayName("Should return localized message when key exists")
    void getMessage_Success() {
        // Given
        String key = "test.key";
        String expectedMessage = "Localized Message";
        when(messageSource.getMessage(eq(key), any(), any(Locale.class)))
                .thenReturn(expectedMessage);

        // When
        var result = messageService.getMessage(key);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedMessage)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return the key itself when MessageSource throws an error")
    void getMessage_Error_ReturnsKey() {
        // Given
        String key = "error.key";
        when(messageSource.getMessage(eq(key), any(), any(Locale.class)))
                .thenThrow(new RuntimeException("Source error"));

        // When
        var result = messageService.getMessage(key);

        // Then
        StepVerifier.create(result)
                .expectNext(key)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return message by HttpStatusCode")
    void getMessageByHttpStatus_Success() {
        // Given
        HttpStatusCode status = HttpStatus.OK;
        String expectedKey = UTILITY_HTTP_RESPONSE_MAP + "200";
        String expectedMessage = "OK Response";

        when(messageSource.getMessage(eq(expectedKey), any(), any(Locale.class)))
                .thenReturn(expectedMessage);

        // When
        var result = messageService.getMessageByHttpStatus(status);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedMessage)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return Transaction ID from headers when present")
    void getTransactionIdLog_HeaderExists() {
        // Given
        String txId = "12345-ABC";
        var request = mock(org.springframework.http.server.reactive.ServerHttpRequest.class);
        var headers = new HttpHeaders();
        headers.add(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID, txId);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);

        // When
        var result = messageService.getTransactionIdLog(exchange);

        // Then
        StepVerifier.create(result)
                .expectNext(txId)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return default message when Transaction ID header is missing")
    void getTransactionIdLog_HeaderMissing() {
        // Given
        var request = mock(org.springframework.http.server.reactive.ServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(new HttpHeaders());

        // When
        var result = messageService.getTransactionIdLog(exchange);

        // Then
        StepVerifier.create(result)
                .expectNext(UTILITY_TRANSACTION_ID_NOT_SPECIFIED)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return generic error message when HttpStatusCodeMessage fails")
    void getHttpStatusCodeMessage_Error_Fallback() {
        // Given
        String code = "500";
        String genericMsg = "Generic Error";

        when(messageSource.getMessage(eq(UTILITY_HTTP_RESPONSE_LITERAL + code), any(), any(Locale.class)))
                .thenThrow(new RuntimeException("Network error"));

        when(messageSource.getMessage(eq(UTILITY_GENERIC_ERROR_MESSAGE), any(), any(Locale.class)))
                .thenReturn(genericMsg);

        // When
        var result = messageService.getHttpStatusCodeMessage(code);

        // Then
        StepVerifier.create(result)
                .expectNext(genericMsg)
                .verifyComplete();
    }
}