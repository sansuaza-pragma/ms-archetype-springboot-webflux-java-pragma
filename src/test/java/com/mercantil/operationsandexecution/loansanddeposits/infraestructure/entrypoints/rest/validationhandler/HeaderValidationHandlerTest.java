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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderValidationHandlerTest implements WithAssertions {

    @Mock
    private Validator validator;

    @Mock
    private ServerRequest serverRequest;

    private TestableHeaderHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestableHeaderHandler(validator);
    }

    @Test
    @DisplayName("Should process request successfully when headers are valid")
    void handleRequest_shouldReturnResponse_whenHeadersAreValid() {
        // Given
        when(validator.validate(any(TestMetadata.class))).thenReturn(Collections.emptySet());

        // When
        Mono<ServerResponse> result = handler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        verify(validator).validate(any(TestMetadata.class));
    }

    @Test
    @DisplayName("Should return error when header validation fails")
    void handleRequest_shouldReturnError_whenHeadersAreInvalid() {
        // Given
        ConstraintViolation<TestMetadata> violation = mock(ConstraintViolation.class);

        // Mock the validator to return a set with one violation
        when(validator.validate(any(TestMetadata.class))).thenReturn(Set.of(violation));

        // When
        Mono<ServerResponse> result = handler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(ConstraintViolationException.class);
                    ConstraintViolationException ex = (ConstraintViolationException) throwable;
                    assertThat(ex.getConstraintViolations()).isNotEmpty();
                })
                .verify();
    }

    @Test
    @DisplayName("Should propagate exception if processing fails after validation")
    void handleRequest_shouldPropagateError_whenProcessingFails() {
        // Given
        when(validator.validate(any(TestMetadata.class))).thenReturn(Collections.emptySet());

        TestableHeaderHandler errorHandler = new TestableHeaderHandler(validator) {
            @Override
            protected Mono<ServerResponse> processRequest(ServerRequest request, TestMetadata metadata) {
                return Mono.error(new RuntimeException("Processing failed"));
            }
        };

        // When
        Mono<ServerResponse> result = errorHandler.handleRequest(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(RuntimeException.class);
                    assertThat(throwable.getMessage()).isEqualTo("Processing failed");
                })
                .verify();
    }

    // =========================================================================
    // Helper Classes (Concrete Implementation & DTO)
    // =========================================================================

    /**
     * Concrete implementation of the abstract class used solely for testing.
     */
    static class TestableHeaderHandler extends HeaderValidationHandler<TestMetadata> {

        protected TestableHeaderHandler(Validator validator) {
            super(validator);
        }

        @Override
        protected TestMetadata extractHeaders(ServerRequest request) {
            return new TestMetadata("some-header-value");
        }

        @Override
        protected Mono<ServerResponse> processRequest(ServerRequest request, TestMetadata metadata) {
            return ServerResponse.ok().build();
        }
    }

    /**
     * Simple DTO to represent the Metadata <M>.
     */
    static class TestMetadata {
        private final String value;

        public TestMetadata(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}