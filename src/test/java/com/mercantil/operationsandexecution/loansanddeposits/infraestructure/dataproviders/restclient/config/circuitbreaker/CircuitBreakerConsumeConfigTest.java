package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.config.circuitbreaker;

import com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker.CircuitBreakerProfilesLoader;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.InstanceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerConsumeConfigTest implements WithAssertions {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CircuitBreakerProfilesLoader profilesLoader;

    @Mock
    private CircuitBreakerRegistry registry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private CircuitBreaker.EventPublisher eventPublisher;

    private CircuitBreakerConsumeConfig config;

    @BeforeEach
    void setUp() {
        config = new CircuitBreakerConsumeConfig(profilesLoader);
    }

    @Test
    @DisplayName("Should successfully entrust and configure the Circuit Breaker bean")
    void shouldEntrustCircuitBreakerBean() {

        InstanceProperties validProperties = new InstanceProperties();
        validProperties.setFailureRateThreshold(50);

        validProperties.setSlidingWindowSize(10);
        validProperties.setMinimumNumberOfCalls(5);
        validProperties.setPermittedNumberOfCallsInHalfOpenState(2);
        validProperties.setWaitDurationInOpenState(10);

        when(profilesLoader.getCachedProfiles().getByPropertiesByKey(anyString()))
                .thenReturn(validProperties);

        when(registry.circuitBreaker(anyString(), any(CircuitBreakerConfig.class)))
                .thenReturn(circuitBreaker);

        when(circuitBreaker.getEventPublisher()).thenReturn(eventPublisher);

        CircuitBreaker result = config.entrustCB(registry);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(circuitBreaker);
        verify(registry).circuitBreaker(anyString(), any(CircuitBreakerConfig.class));
        verify(eventPublisher).onStateTransition(any());
    }

    @Test
    void LogCircuitBreakerStateChange() throws Exception {
        CircuitBreakerOnStateTransitionEvent event = new CircuitBreakerOnStateTransitionEvent(
                "testCB",
                CircuitBreaker.StateTransition.transitionBetween("testCB", CircuitBreaker.State.CLOSED, CircuitBreaker.State.OPEN)
        );

        Method method = CircuitBreakerConsumeConfig.class
                .getDeclaredMethod("logCircuitBreakerStateChange", CircuitBreakerOnStateTransitionEvent.class);
        method.setAccessible(true);

        method.invoke(config, event);
    }

}

