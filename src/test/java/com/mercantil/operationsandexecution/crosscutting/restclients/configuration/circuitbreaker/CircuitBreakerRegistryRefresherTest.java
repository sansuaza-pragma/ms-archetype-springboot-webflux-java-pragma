package com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.CircuitBreakerProfileProperties;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.InstanceProperties;
import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.config.circuitbreaker.CircuitBreakerRegistreredEnum;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerRegistryRefresherTest implements WithAssertions {

    @Mock
    private CircuitBreakerRegistry registry;

    @Mock
    private CircuitBreakerProfilesLoader profilesLoader;

    @Mock
    private CircuitBreaker circuitBreaker;

    private CircuitBreakerRegistryRefresher refresher;

    @BeforeEach
    void setUp() {
        refresher = new CircuitBreakerRegistryRefresher(registry, profilesLoader);
    }


    @Test
    void shouldReloadCircuitBreakersWithNewProfiles() {
        String cbName = "MY_SERVICE";
        String profileKey = "my-service";
        when(circuitBreaker.getName()).thenReturn(cbName);

        try (MockedStatic<CircuitBreakerRegistreredEnum> mockedStatic = mockStatic(CircuitBreakerRegistreredEnum.class)) {
            CircuitBreakerRegistreredEnum mockEnum = mock(CircuitBreakerRegistreredEnum.class, RETURNS_DEEP_STUBS);
            mockedStatic.when(() -> CircuitBreakerRegistreredEnum.getProfileKeyByName(anyString())).thenReturn(mockEnum);
            when(mockEnum.getProfileKey().getProfileYamlNotation()).thenReturn(profileKey);

            Map<String, InstanceProperties> profilesMap = new LinkedHashMap<>();
            profilesMap.put(profileKey, new InstanceProperties(10, 20, 3, 5, 2));
            CircuitBreakerProfileProperties profiles = new CircuitBreakerProfileProperties();
            profiles.setProfiles(profilesMap);

            when(profilesLoader.reloadFromFile()).thenReturn(Mono.just(profiles));
            when(registry.getAllCircuitBreakers()).thenReturn(Set.of(circuitBreaker));

            refresher.reloadCircuitBreakers().block();

            verify(registry).remove(cbName);
            verify(registry).addConfiguration(eq(cbName), any());
            verify(registry).circuitBreaker(cbName);
        }
    }

    @Test
    void shouldNotUpdateIfNoMatchingProfile() {
        String cbName = "OTHER_SERVICE";
        when(circuitBreaker.getName()).thenReturn(cbName);

        try (MockedStatic<CircuitBreakerRegistreredEnum> mockedStatic = mockStatic(CircuitBreakerRegistreredEnum.class)) {
            CircuitBreakerRegistreredEnum mockEnum = mock(CircuitBreakerRegistreredEnum.class, RETURNS_DEEP_STUBS);
            mockedStatic.when(() -> CircuitBreakerRegistreredEnum.getProfileKeyByName(anyString())).thenReturn(mockEnum);
            when(mockEnum.getProfileKey().getProfileYamlNotation()).thenReturn("my-service");

            Map<String, InstanceProperties> profilesMap = Collections.emptyMap();
            CircuitBreakerProfileProperties profiles = new CircuitBreakerProfileProperties();
            profiles.setProfiles(profilesMap);

            when(profilesLoader.reloadFromFile()).thenReturn(Mono.just(profiles));
            when(registry.getAllCircuitBreakers()).thenReturn(Set.of(circuitBreaker));

            refresher.reloadCircuitBreakers().block();

            verify(registry, never()).remove(anyString());
            verify(registry, never()).addConfiguration(anyString(), any());
            verify(registry, never()).circuitBreaker(anyString());
        }
    }

}