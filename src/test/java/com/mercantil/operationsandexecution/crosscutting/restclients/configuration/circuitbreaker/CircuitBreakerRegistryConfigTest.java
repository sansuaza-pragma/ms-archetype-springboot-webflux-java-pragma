package com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerRegistryConfigTest implements WithAssertions {

    private CircuitBreakerRegistryConfig config;

    @BeforeEach
    void setUp() {
        config = new CircuitBreakerRegistryConfig();
    }

    @Test
    void shouldCreateDefaultCircuitBreakerRegistry() {
        CircuitBreakerRegistry registry = config.circuitBreakerRegistry();
        assertThat(registry).isNotNull();
        assertThat(registry).isInstanceOf(CircuitBreakerRegistry.class);
    }
}