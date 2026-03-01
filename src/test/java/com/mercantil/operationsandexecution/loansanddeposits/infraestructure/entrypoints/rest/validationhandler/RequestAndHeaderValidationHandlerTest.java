package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.entrypoints.rest.validationhandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAndHeaderValidationHandlerTest implements WithAssertions {

    @Mock
    private Validator validator;

    @Mock
    private ServerRequest serverRequest;

    private TestHandler validationHandler;

    @BeforeEach
    void setUp() {
        validationHandler = new TestHandler(TestBody.class, validator);
    }

    @Test
    @DisplayName("Should process request successfully when body and headers are valid")
    void handleRequest_shouldReturnResponse_whenBodyAndHeadersAreValid() {
        // Given
        TestBody validBody = new TestBody("valid-content");

        when(serverRequest.bodyToMono(TestBody.class)).thenReturn(Mono.just(validBody));
        when(validator.validate(any(TestHeader.class))).thenReturn(Collections.emptySet());
        when(validator.validate(any(TestBody.class))).thenReturn(Collections.emptySet());

        // When
        Mono<ServerResponse> result = validationHandler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw ServerWebInputException when request body is empty")
    void handleRequest_shouldThrowException_whenBodyIsEmpty() {
        // Given
        when(serverRequest.bodyToMono(TestBody.class)).thenReturn(Mono.empty());

        // When
        Mono<ServerResponse> result = validationHandler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(ServerWebInputException.class);
                    // We assume "BODY_REQUIRED_MESSAGE" is passed to the exception
                    assertThat(throwable.getMessage()).contains("400 BAD_REQUEST");
                })
                .verify();
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when headers are invalid")
    void handleRequest_shouldThrowException_whenHeadersAreInvalid() {
        // Given
        TestBody validBody = new TestBody("valid-content");
        ConstraintViolation<TestHeader> violation = mock(ConstraintViolation.class);

        when(serverRequest.bodyToMono(TestBody.class)).thenReturn(Mono.just(validBody));
        when(validator.validate(any(TestHeader.class))).thenReturn(Set.of(violation));

        // When
        Mono<ServerResponse> result = validationHandler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw ConstraintViolationException when body is invalid")
    void handleRequest_shouldThrowException_whenBodyIsInvalid() {
        // Given
        TestBody invalidBody = new TestBody("invalid-content");
        ConstraintViolation<TestBody> violation = mock(ConstraintViolation.class);

        when(serverRequest.bodyToMono(TestBody.class)).thenReturn(Mono.just(invalidBody));
        when(validator.validate(any(TestHeader.class))).thenReturn(Collections.emptySet());
        when(validator.validate(any(TestBody.class))).thenReturn(Set.of(violation));

        // When
        Mono<ServerResponse> result = validationHandler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    @DisplayName("Should skip header validation if headers are null")
    void handleRequest_shouldSkipHeaderValidation_whenExtractHeadersReturnsNull() {
        // Given
        TestHandler nullHeaderHandler = new TestHandler(TestBody.class, validator) {
            @Override
            protected TestHeader extractHeaders(TestBody body, ServerRequest request) {
                return null;
            }
        };

        TestBody validBody = new TestBody("valid-content");
        when(serverRequest.bodyToMono(TestBody.class)).thenReturn(Mono.just(validBody));
        when(validator.validate(any(TestBody.class))).thenReturn(Collections.emptySet());

        // When
        Mono<ServerResponse> result = nullHeaderHandler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }


    /**
     * Concrete implementation of the abstract class solely for testing purposes.
     */
    static class TestHandler extends RequestAndHeaderValidationHandler<TestBody, TestHeader> {

        protected TestHandler(Class<TestBody> clazz, Validator validator) {
            super(clazz, validator);
        }

        @Override
        protected TestHeader extractHeaders(TestBody body, ServerRequest request) {
            return new TestHeader("test-header-value");
        }

        @Override
        protected Mono<ServerResponse> processBody(ValidatedRequest<TestBody, TestHeader> validatedRequest, ServerRequest originalRequest) {
            return ServerResponse.ok().build();
        }
    }

    static class TestBody {
        String content;

        public TestBody(String content) {
            this.content = content;
        }
    }

    static class TestHeader {
        String meta;

        public TestHeader(String meta) {
            this.meta = meta;
        }
    }
}