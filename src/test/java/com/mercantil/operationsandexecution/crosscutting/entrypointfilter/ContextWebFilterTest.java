package com.mercantil.operationsandexecution.crosscutting.entrypointfilter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextWebFilterTest {
    @InjectMocks
    private ContextWebFilter contextWebFilter;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders headers;

    @Test
    void filter_WithValidTransactionId_ShouldAddContext() {
        // Arrange
        String transactionId = "test-transaction-id";
        String endpoint = "/test/endpoint";
        URI uri = URI.create(endpoint);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(uri);
        when(headers.getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID)).thenReturn(transactionId);
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(contextWebFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(request).getHeaders();
        verify(request).getURI();
        verify(headers).getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID);
    }

    @Test
    void filter_WithNullTransactionId_ShouldUseDefaultValue() {
        // Arrange
        String endpoint = "/test/endpoint";
        URI uri = URI.create(endpoint);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(uri);
        when(headers.getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID)).thenReturn(null);
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(contextWebFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(request).getHeaders();
        verify(request).getURI();
        verify(headers).getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID);
    }

    @Test
    void filter_WithEmptyTransactionId_ShouldUseDefaultValue() {
        // Arrange
        String endpoint = "/test/endpoint";
        URI uri = URI.create(endpoint);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(uri);
        when(headers.getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID)).thenReturn("");
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(contextWebFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(request).getHeaders();
        verify(request).getURI();
        verify(headers).getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID);
    }

    @Test
    void filter_ChainError_ShouldPropagateError() {
        // Arrange
        String transactionId = "test-transaction-id";
        String endpoint = "/test/endpoint";
        URI uri = URI.create(endpoint);
        RuntimeException testError = new RuntimeException("Test error");

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(uri);
        when(headers.getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID)).thenReturn(transactionId);
        when(chain.filter(any())).thenReturn(Mono.error(testError));

        // Act & Assert
        StepVerifier.create(contextWebFilter.filter(exchange, chain))
                .expectError(RuntimeException.class)
                .verify();

        verify(chain).filter(exchange);
        verify(request).getHeaders();
        verify(request).getURI();
        verify(headers).getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID);
    }
}