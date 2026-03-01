package com.mercantil.operationsandexecution.crosscutting.restclients.utility;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllowedHostsWebClientFilterTest implements WithAssertions {

    @Mock
    private HostAccessValidator hostAccessValidator;

    @Mock
    private ExchangeFunction exchangeFunction;

    @Mock
    private ClientResponse clientResponse;

    private AllowedHostsWebClientFilter allowedHostsWebClientFilter;

    @BeforeEach
    void setUp() {
        allowedHostsWebClientFilter = new AllowedHostsWebClientFilter(hostAccessValidator);
    }


    @Test
    void shouldAllowRequestWhenHostIsValid() {
        // given
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://allowed-host.com")).build();
        when(hostAccessValidator.validateHostAccess("allowed-host.com")).thenReturn(Mono.empty());
        when(exchangeFunction.exchange(request)).thenReturn(Mono.just(clientResponse));

        // when
        Mono<ClientResponse> result = allowedHostsWebClientFilter.filter(request, exchangeFunction);

        // then
        assertThat(result.block()).isEqualTo(clientResponse);
        verify(hostAccessValidator).validateHostAccess("allowed-host.com");
        verify(exchangeFunction).exchange(request);
    }
}