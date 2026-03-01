package com.mercantil.operationsandexecution.crosscutting.entrypointfilter;


import com.mercantil.operationsandexecution.crosscutting.entrypointfilter.constant.HandleSecurityConstants;
import com.mercantil.operationsandexecution.crosscutting.logging.model.ContextDataKey;
import com.mercantil.operationsandexecution.crosscutting.logging.model.MetaData;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class HandleSecurityWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String transactionId = extractTransactionId(exchange);
        applySecurityHeaders(exchange);

        String endpoint = exchange.getRequest().getURI().getPath();

        return chain.filter(exchange)
                .contextWrite(Context.of(ContextDataKey.META_DATA, new MetaData(transactionId, endpoint)));
    }

    private String extractTransactionId(ServerWebExchange exchange) {
        String headerValue = exchange.getRequest().getHeaders().getFirst(HandleSecurityConstants.HEADER_TRANSACTION_ID);
        return (headerValue != null && !headerValue.isBlank())
                ? headerValue
                : HandleSecurityConstants.UTILITY_TRANSACTION_ID_NOT_SPECIFIED;
    }

    private Mono<Void> applySecurityHeaders(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set(HandleSecurityConstants.X_CONTENT_TYPE_OPTIONS, HandleSecurityConstants.VALUE_NOSNIFF);
        headers.set(HandleSecurityConstants.X_FRAME_OPTIONS, HandleSecurityConstants.VALUE_DENY);
        headers.set(HandleSecurityConstants.CONTENT_SECURITY_POLICY, HandleSecurityConstants.VALUE_CSP);
        headers.set(HandleSecurityConstants.STRICT_TRANSPORT_SECURITY, HandleSecurityConstants.VALUE_HSTS);
        headers.set(HandleSecurityConstants.REFERRER_POLICY, HandleSecurityConstants.VALUE_REFERRER);
        headers.set(HandleSecurityConstants.PERMISSIONS_POLICY, HandleSecurityConstants.VALUE_PERMISSIONS);
        headers.set(HandleSecurityConstants.CACHE_CONTROL, HandleSecurityConstants.VALUE_NO_CACHE);
        headers.set(HandleSecurityConstants.PRAGMA, HandleSecurityConstants.VALUE_PRAGMA);
        headers.set(HandleSecurityConstants.EXPIRES, HandleSecurityConstants.VALUE_EXPIRES);
        headers.set(HandleSecurityConstants.X_XSS_PROTECTION, HandleSecurityConstants.VALUE_XXSS);

        return Mono.empty();
    }
}
