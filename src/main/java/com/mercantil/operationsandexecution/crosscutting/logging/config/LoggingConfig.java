package com.mercantil.operationsandexecution.crosscutting.logging.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;

/**
 * Configuración de beans relacionados con logging y telemetría.
 */
@Configuration
public class LoggingConfig {

    /**
     * Crea e inyecta un bean de {@link com.microsoft.applicationinsights.TelemetryClient} configurado con la clave de instrumentación
     * y el nombre de la aplicación para Azure Application Insights.
     *
     * @param instrumentationKey Clave de instrumentación de Application Insights.
     * @param msName             Nombre de la aplicación (service name).
     * @return Cliente de telemetría configurado.
     */
    @Bean
    public TelemetryClient telemetryClient(
            @Value("${azure.application-insights.instrumentation-key}") String instrumentationKey,
            @Value("${spring.application.name}") String msName) {
        TelemetryConfiguration configuration = TelemetryConfiguration.getActive();
        configuration.setInstrumentationKey(instrumentationKey);
        configuration.setRoleName(msName);
        return new TelemetryClient(configuration);
    }

    /**
     * Crea e inyecta un bean de {@link Gson} personalizado para serializar objetos {@link java.time.ZonedDateTime}
     * como cadenas ISO-8601.
     *
     * @return Instancia de Gson personalizada.
     */
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, (JsonSerializer<ZonedDateTime>)
                        (src, typeOfSrc, context) ->
                                new JsonPrimitive(src.toString()))
                .create();
    }

}