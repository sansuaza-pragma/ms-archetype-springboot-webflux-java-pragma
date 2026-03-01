package com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.InstanceProperties;
import org.assertj.core.api.WithAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerProfilesLoaderTest implements WithAssertions {

    @Mock
    private Resource resource;

    private CircuitBreakerProfilesLoader loader;

    @BeforeEach
    void setUp() {
        loader = new CircuitBreakerProfilesLoader();
        try {
            var field = CircuitBreakerProfilesLoader.class.getDeclaredField("resilienceProfilesResource");
            field.setAccessible(true);
            field.set(loader, resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldLoadProfilesSuccessfully() throws Exception {
        String yamlContent = "profiles:\n  test:\n    failureRateThreshold: 10\n    waitDurationInOpenState: 20\n    permittedNumberOfCallsInHalfOpenState: 3\n    slidingWindowSize: 5\n    minimumNumberOfCalls: 2\n";
        InputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes());
        when(resource.getInputStream()).thenReturn(inputStream);

        StepVerifier.create(loader.reloadFromFile())
                .assertNext(profiles -> {
                    assertThat(profiles).isNotNull();
                    Map<String, InstanceProperties> map = profiles.getProfiles();
                    assertThat(map).containsKey("test");
                    assertThat(map.get("test").getFailureRateThreshold()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnBackupProfilesOnError() throws Exception {
        when(resource.getInputStream()).thenThrow(new RuntimeException("File not found"));

        StepVerifier.create(loader.reloadFromFile())
                .assertNext(profiles -> {
                    assertThat(profiles).isNotNull();
                    assertThat(profiles.getProfiles()).containsKeys(
                            "high-critical-service",
                            "high-non-critical-service",
                            "medium-critical-service",
                            "medium-non-critical-service"
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldUseBackupProfilesWhenYamlIsEmpty() throws Exception {
        String yamlContent = "profiles: {}";
        InputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes());
        when(resource.getInputStream()).thenReturn(inputStream);

        loader.init();

        Awaitility.await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(loader.getCachedProfiles()).isNotNull());

        assertThat(loader.getCachedProfiles().getProfiles())
                .containsKeys("high-critical-service", "high-non-critical-service",
                        "medium-critical-service", "medium-non-critical-service");
    }

    @Test
    void shouldThrowExceptionWhenYamlIsEmptyInDirectLoad() throws Exception {
        String yamlContent = "profiles: {}";
        InputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes());
        when(resource.getInputStream()).thenReturn(inputStream);

        var method = CircuitBreakerProfilesLoader.class.getDeclaredMethod("loadProfilesFromResource", Resource.class);
        method.setAccessible(true);
        assertThatThrownBy(() -> method.invoke(loader, resource))
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("The YAML file is empty or has an invalid format");
    }
}