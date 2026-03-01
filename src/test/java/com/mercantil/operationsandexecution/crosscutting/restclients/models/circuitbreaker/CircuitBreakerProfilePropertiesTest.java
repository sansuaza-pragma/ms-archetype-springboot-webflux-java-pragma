package com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerProfilePropertiesTest implements WithAssertions {

    private CircuitBreakerProfileProperties properties;

    @BeforeEach
    void setUp() {
        properties = new CircuitBreakerProfileProperties();
    }

    @Test
    void shouldReturnInstancePropertiesByKey() {
        InstanceProperties instance = new InstanceProperties();
        properties.setProfiles(Map.of("test-profile", instance));

        InstanceProperties result = properties.getByPropertiesByKey("test-profile");

        assertThat(result).isSameAs(instance);
    }

    @Test
    void shouldReturnNullIfKeyDoesNotExist() {
        InstanceProperties result = properties.getByPropertiesByKey("non-existent");
        assertThat(result).isNull();
    }

    @Test
    void shouldSetAndGetProfilesMap() {
        InstanceProperties instance = new InstanceProperties();
        properties.setProfiles(Map.of("profile1", instance));

        assertThat(properties.getProfiles()).containsEntry("profile1", instance);
    }
}