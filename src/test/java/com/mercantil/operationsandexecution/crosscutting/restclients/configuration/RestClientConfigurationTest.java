package com.mercantil.operationsandexecution.crosscutting.restclients.configuration;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class RestClientConfigurationTest implements WithAssertions {

    private RestClientConfiguration restClientConfiguration;
    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        restClientConfiguration = new RestClientConfiguration();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldCreateWebClientAndTriggerConnectionHandlers() {
        String allowedHosts = "localhost";
        int connectionTimeout = 1000;
        int readTimeout = 1000;
        WebClient webClient = restClientConfiguration.webClient(allowedHosts, connectionTimeout, readTimeout);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        StepVerifier.create(
                        webClient.get()
                                .uri(mockWebServer.url("/test").uri())
                                .retrieve()
                                .toBodilessEntity()
                )
                .expectNextCount(1)
                .verifyComplete();
    }
}