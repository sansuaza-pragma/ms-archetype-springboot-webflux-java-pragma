package com.mercantil.operationsandexecution.crosscutting.entrypointfilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HandleSecurityWebFilterTest {

    private HandleSecurityWebFilter filter;

    @BeforeEach
    void setUp() {
        filter = new HandleSecurityWebFilter();
    }

    @Test
    void filter_shouldApplyHeadersAndContinueChain() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        HttpHeaders headers = new HttpHeaders();
        HttpHeaders responseHeaders = new HttpHeaders();
        WebFilterChain chain = mock(WebFilterChain.class);

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("/test"));
        when(response.getHeaders()).thenReturn(responseHeaders);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertTrue(responseHeaders.containsKey("X-Content-Type-Options"));
        assertTrue(responseHeaders.containsKey("X-Frame-Options"));
        assertTrue(responseHeaders.containsKey("Content-Security-Policy"));
        assertTrue(responseHeaders.containsKey("Strict-Transport-Security"));
        assertTrue(responseHeaders.containsKey("Referrer-Policy"));
        assertTrue(responseHeaders.containsKey("Permissions-Policy"));
        assertTrue(responseHeaders.containsKey("Cache-Control"));
        assertTrue(responseHeaders.containsKey("Pragma"));
        assertTrue(responseHeaders.containsKey("Expires"));
        assertTrue(responseHeaders.containsKey("X-XSS-Protection"));
    }

    @Test
    void filter_shouldHandleTransactionIdFromHeader() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        HttpHeaders headers = new HttpHeaders();
        HttpHeaders responseHeaders = new HttpHeaders();
        WebFilterChain chain = mock(WebFilterChain.class);

        headers.add("transactionId", "trx-123");

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("/test"));
        when(response.getHeaders()).thenReturn(responseHeaders);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_shouldHandleMissingTransactionId() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        HttpHeaders headers = new HttpHeaders();
        HttpHeaders responseHeaders = new HttpHeaders();
        WebFilterChain chain = mock(WebFilterChain.class);

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("/test"));
        when(response.getHeaders()).thenReturn(responseHeaders);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }
}
