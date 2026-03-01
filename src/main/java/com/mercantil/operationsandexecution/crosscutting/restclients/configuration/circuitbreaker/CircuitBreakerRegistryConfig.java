package com.mercantil.operationsandexecution.crosscutting.restclients.configuration.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerRegistryConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(){
        return CircuitBreakerRegistry.ofDefaults();
    }
}
