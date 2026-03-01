package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.entrypoints.rest.routerhandler;

import com.mercantil.operationsandexecution.crosscutting.messages.implementation.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CreateLoanHandler {
    private final IMessageService messageService;

    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        // TODO: 22/01/25  Agregar headers mandatorios.

        return null;
    }
}
