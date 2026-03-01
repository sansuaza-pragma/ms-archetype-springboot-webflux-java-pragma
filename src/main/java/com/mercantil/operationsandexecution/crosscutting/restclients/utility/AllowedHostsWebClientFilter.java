package com.mercantil.operationsandexecution.crosscutting.restclients.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * Filtro para WebClient que valida si el host de destino está permitido antes de realizar la petición.
 * Si el host no está en la lista de permitidos, bloquea la solicitud lanzando una excepción de seguridad.
 *
 * @since 1.0
 */
@RequiredArgsConstructor
public class AllowedHostsWebClientFilter implements ExchangeFilterFunction {

    private final HostAccessValidator hostAccessValidator;

    /**
     * Intercepta la petición HTTP y valida el host de destino.
     *
     * @param request petición HTTP a procesar
     * @param next función para continuar el flujo si el host es válido
     * @return Mono con la respuesta si el host está permitido, Mono con error si no
     */
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String host = request.url().getHost();
        return hostAccessValidator.validateHostAccess(host)
            .then(next.exchange(request));
    }
}