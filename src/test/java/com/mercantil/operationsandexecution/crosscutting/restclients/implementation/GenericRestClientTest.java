package com.mercantil.operationsandexecution.crosscutting.restclients.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercantil.operationsandexecution.crosscutting.logging.ILoggerService;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.HttpRequestConfiguration;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.StandardRestException;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.model.ApiException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericRestClientTest implements WithAssertions {

    private GenericRestClient genericRestClient;
    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private ILoggerService loggerService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        lenient().when(loggerService.logTraceInfo(any(), anyString(), any())).thenReturn(Mono.empty());
        lenient().when(loggerService.logWarning(any(), any())).thenReturn(Mono.empty());

        genericRestClient = new GenericRestClient(webClient, objectMapper, circuitBreakerRegistry, loggerService);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void sendRequestAndReceiveResponse_shouldReturnSuccessfulResponse_whenRequestIsValid() {
        var transactionId = "TXN-12345";
        var responseBody = "{\"message\": \"Success\", \"code\": 200}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> requestConfig = createRequestConfig();
        URI uri = mockWebServer.url("/api/test").uri();

        var result = genericRestClient.sendRequestAndReceiveResponse(requestConfig, transactionId, uri);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    Assertions.assertNotNull(response.getBody());
                    assertThat(response.getBody().getMessage()).isEqualTo("Success");
                })
                .verifyComplete();
    }

    @Test
    void sendRequestAndReceiveResponse_shouldThrowStandardRestException_whenServerReturnsError() {
        var errorBody = "{\"errorCode\": \"ERR_001\", \"errorMessage\": \"Invalid request\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400).setBody(errorBody)
                .addHeader("Content-Type", "application/json"));

        HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> requestConfig = createRequestConfig();
        URI uri = mockWebServer.url("/api/test").uri();


        var result = genericRestClient.sendRequestAndReceiveResponse(requestConfig, "TXN-ERR", uri);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof StandardRestException &&
                        ((StandardRestException) throwable).getStatusCode().equals(HttpStatus.BAD_REQUEST))
                .verify();
    }


    @Test
    void sendRequestWithCB_shouldReturnSuccess_whenCircuitIsClosedAndRequestIsValid() {
        var cbName = "test-cb";
        var transactionId = "TXN-CB-OK";
        setupCircuitBreakerMock(cbName, Duration.ofSeconds(5));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"message\": \"Success\", \"code\": 200}")
                .addHeader("Content-Type", "application/json"));

        HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> requestConfig = createRequestConfig();
        URI uri = mockWebServer.url("/api/cb-test").uri();

        var result = genericRestClient.sendRequestAndReceiveResponse(requestConfig, transactionId, uri, cbName);

        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void sendRequestWithCB_shouldThrowApiException_whenTimeoutOccurs() {
        var cbName = "slow-cb";
        var transactionId = "TXN-TIMEOUT";

        setupCircuitBreakerMock(cbName, Duration.ofMillis(100));

        mockWebServer.enqueue(new MockResponse()
                .setBody("ok")
                .setBodyDelay(500, TimeUnit.MILLISECONDS));

        HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> requestConfig = createRequestConfig();
        URI uri = mockWebServer.url("/api/timeout").uri();

        var result = genericRestClient.sendRequestAndReceiveResponse(requestConfig, transactionId, uri, cbName);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ApiException &&
                                "SERVER_TOO_SLOW".equals(throwable.getMessage())
                )
                .verify();
    }


    @Test
    void sendRequestAndReceiveResponse_shouldHandleEmptyErrorBody_whenServerReturnsErrorWithNoContent() {
        var transactionId = "TXN-EMPTY-BODY";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json"));

        HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> requestConfig = createRequestConfig();
        URI uri = mockWebServer.url("/api/empty-error").uri();

        var result = genericRestClient.sendRequestAndReceiveResponse(requestConfig, transactionId, uri);

       StepVerifier.create(result)
           .expectErrorMatches(throwable ->
               throwable instanceof StandardRestException &&
               ((StandardRestException) throwable).getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR) &&
               ((StandardRestException) throwable).getErrorResponseBody().equals(HttpStatus.INTERNAL_SERVER_ERROR)
           )
           .verify();
    }


    @Test
    void sendRequestWithCB_shouldThrowApiException_whenCircuitBreakerIsOpen() {
        var cbName = "open-cb";
        var transactionId = "TXN-OPEN";
        var openCircuitBreaker = CircuitBreaker.of(cbName, CircuitBreakerConfig.custom().build());
        openCircuitBreaker.transitionToOpenState();

        when(circuitBreakerRegistry.circuitBreaker(cbName)).thenReturn(openCircuitBreaker);

        HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> requestConfig = createRequestConfig();
        URI uri = mockWebServer.url("/api/open").uri();

        var result = genericRestClient.sendRequestAndReceiveResponse(requestConfig, transactionId, uri, cbName);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ApiException &&
                                throwable.getMessage().contains("Service temporarily unavailable")
                )
                .verify();
    }

    @Test
    void sendRequestWithCB_shouldThrowStandardRestException_whenServerReturnsError() {
        var cbName = "error-cb";
        setupCircuitBreakerMock(cbName, Duration.ofSeconds(2));
        var errorBody = "{\"errorCode\": \"ERR_CB\", \"errorMessage\": \"CB Error\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(errorBody)
                .addHeader("Content-Type", "application/json"));

        HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> requestConfig = createRequestConfig();
        URI uri = mockWebServer.url("/api/cb-error").uri();

        var result = genericRestClient.sendRequestAndReceiveResponse(requestConfig, "TXN-ERR", uri, cbName);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof StandardRestException &&
                                ((StandardRestException) throwable).getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)
                )
                .verify();
    }


    private void setupCircuitBreakerMock(String name, Duration timeoutDuration) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slowCallDurationThreshold(timeoutDuration)
                .build();
        CircuitBreaker circuitBreaker = CircuitBreaker.of(name, config);
        when(circuitBreakerRegistry.circuitBreaker(name)).thenReturn(circuitBreaker);
    }

    private HttpRequestConfiguration<SuccessResponse, ErrorResponse, RequestBody> createRequestConfig() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        HttpRequestConfiguration.HttpResponsePayload<SuccessResponse, ErrorResponse> responsePayload =
                new HttpRequestConfiguration.HttpResponsePayload<>(SuccessResponse.class, ErrorResponse.class);

        return new HttpRequestConfiguration<>(
                HttpMethod.POST,
                headers,
                new RequestBody("test"),
                EnumActionLogs.GENERIC_ERROR, // Asegúrate de tener este Enum disponible
                responsePayload
        );
    }

    static class SuccessResponse {
        private String message;
        private Integer code;

        public SuccessResponse() {
        }

        public SuccessResponse(String m, Integer c) {
            this.message = m;
            this.code = c;
        }

        public String getMessage() {
            return message;
        }

        public Integer getCode() {
            return code;
        }
    }

    static class ErrorResponse {
        private String errorCode;
        private String errorMessage;

        public ErrorResponse() {
        }

        public String getErrorCode() {
            return errorCode;
        }
    }

    static class RequestBody {
        private String value;

        public RequestBody(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}