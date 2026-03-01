package com.mercantil.operationsandexecution.crosscutting.restclients.utility;

import com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HostAccessValidatorTest implements WithAssertions {

    private HostAccessValidator hostAccessValidator;

    @BeforeEach
    void setUp() {
        hostAccessValidator = new HostAccessValidator("allowed.com,another.com");
    }

    @Test
    void shouldAllowAccessForAllowedHost() {
        Mono<Void> result = hostAccessValidator.validateHostAccess("allowed.com");
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldDenyAccessForNotAllowedHost() {
        Mono<Void> result = hostAccessValidator.validateHostAccess("forbidden.com");
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof SecurityException &&
                                error.getMessage().equals(RestClientConstants.UTILITY_SECURITY_HOST_ERROR_MESSAGE + "forbidden.com")
                )
                .verify();
    }

    @Test
    void shouldAllowAccessForAllowedHostWithSpaces() {
        hostAccessValidator = new HostAccessValidator(" allowed.com , another.com ");
        Mono<Void> result = hostAccessValidator.validateHostAccess("allowed.com");
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldDenyAccessForHostThatIsSubstringOfAllowed() {
        Mono<Void> result = hostAccessValidator.validateHostAccess("allowed");
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof SecurityException &&
                                error.getMessage().equals(RestClientConstants.UTILITY_SECURITY_HOST_ERROR_MESSAGE + "allowed")
                )
                .verify();
    }

    @Test
    void shouldDenyAccessWhenAllowedHostsListIsEmpty() {
        hostAccessValidator = new HostAccessValidator("");
        Mono<Void> result = hostAccessValidator.validateHostAccess("anyhost.com");
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof SecurityException &&
                                error.getMessage().equals(RestClientConstants.UTILITY_SECURITY_HOST_ERROR_MESSAGE + "anyhost.com")
                )
                .verify();
    }

    @Test
    void shouldDenyAccessForCaseSensitiveHost() {
        Mono<Void> result = hostAccessValidator.validateHostAccess("ALLOWED.COM");
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof SecurityException &&
                                error.getMessage().equals(RestClientConstants.UTILITY_SECURITY_HOST_ERROR_MESSAGE + "ALLOWED.COM")
                )
                .verify();
    }


}