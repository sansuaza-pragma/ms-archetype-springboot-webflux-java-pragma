package com.mercantil.operationsandexecution.crosscutting.restclients.utility;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.StandardRestException;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.InstanceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerConfigBuilderTest implements WithAssertions {

    private InstanceProperties instanceProperties;

    @BeforeEach
    void setUp() {
        instanceProperties = new InstanceProperties();
        instanceProperties.setFailureRateThreshold(50);
        instanceProperties.setWaitDurationInOpenState(10);
        instanceProperties.setSlidingWindowSize(20);
        instanceProperties.setMinimumNumberOfCalls(5);
        instanceProperties.setPermittedNumberOfCallsInHalfOpenState(3);
    }

    @Test
    void shouldBuildCustomCircuitBreakerConfig() {
        CircuitBreakerConfig config = CircuitBreakerConfigBuilder.buildEachCircuitBreakerConfig(instanceProperties);

        assertThat(config.getFailureRateThreshold()).isEqualTo(50);
        assertThat(config.getSlidingWindowSize()).isEqualTo(20);
        assertThat(config.getMinimumNumberOfCalls()).isEqualTo(5);
        assertThat(config.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(3);
        assertThat(config.getSlidingWindowType()).isEqualTo(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED);
    }

    @Test
    void shouldReturnTrueWhenStandardRestExceptionWith5xxStatus() {
        StandardRestException ex = new StandardRestException(HttpStatus.INTERNAL_SERVER_ERROR, null);
        boolean result = invokeIsServerErrorException(ex);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenNotStandardRestException() {
        Throwable ex = new RuntimeException("Some error");
        boolean result = invokeIsServerErrorException(ex);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStandardRestExceptionWithNon5xxStatus() {
        StandardRestException ex = new StandardRestException(HttpStatus.BAD_REQUEST, null);
        boolean result = invokeIsServerErrorException(ex);
        assertThat(result).isFalse();
    }

    private boolean invokeIsServerErrorException(Throwable throwable) {
        try {
           var method = CircuitBreakerConfigBuilder.class.getDeclaredMethod("isServerErrorException", Throwable.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, throwable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}