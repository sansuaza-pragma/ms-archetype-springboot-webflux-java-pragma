package com.mercantil.operationsandexecution.crosscutting.entrypointfilter;


import com.mercantil.operationsandexecution.crosscutting.logging.model.ContextDataKey;
import com.mercantil.operationsandexecution.crosscutting.logging.model.MetaData;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class ContextWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            String transactionId = exchange.getRequest().getHeaders()
                    .getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID);
            if (transactionId == null || transactionId.isEmpty()) {
                transactionId = "NOT_SPECIFIED";
            }
            String endpoint = exchange.getRequest().getURI().getPath();
            return chain.filter(exchange)
                    .contextWrite(Context.of(ContextDataKey.META_DATA,
                            new MetaData(transactionId, endpoint)));
        });
    }
}
