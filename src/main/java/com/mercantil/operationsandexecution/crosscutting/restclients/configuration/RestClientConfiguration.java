package com.mercantil.operationsandexecution.crosscutting.restclients.configuration;


import com.mercantil.operationsandexecution.crosscutting.restclients.utility.AllowedHostsWebClientFilter;
import com.mercantil.operationsandexecution.crosscutting.restclients.utility.HostAccessValidator;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de WebClient con filtro de validación de hosts permitidos.
 * El filtro bloquea las solicitudes a hosts no autorizados según la lista definida en la propiedad de configuración.
 *
 * @since 1.0
 */
@Configuration
public class RestClientConfiguration {

    /**
     * Crea un bean de WebClient con filtro de validación de hosts.
     *
     * @param allowedHosts lista de hosts permitidos, obtenida de la configuración
     * @return instancia de WebClient con filtro de seguridad
     */
    @Bean
    public WebClient webClient(
            @Value("${rest.allowed-host.host-name}") String allowedHosts,
            @Value("${rest.connection-timeout-in-millis}") Integer connectionTimeout,
            @Value("${rest.read-timeout-in-millis}") Integer readTimeout
    ) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));


        HostAccessValidator validator = new HostAccessValidator(allowedHosts);
        ExchangeFilterFunction allowedHostsFilter = new AllowedHostsWebClientFilter(validator);


        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(allowedHostsFilter)
                .build();
    }
}