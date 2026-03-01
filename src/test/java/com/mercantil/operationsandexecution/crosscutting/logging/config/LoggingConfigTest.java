package com.mercantil.operationsandexecution.crosscutting.logging.config;

import com.google.gson.Gson;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingConfigTest {

    private final LoggingConfig config = new LoggingConfig();

    @AfterEach
    void resetTelemetryConfig() {
        TelemetryConfiguration.getActive().setInstrumentationKey("test-key");
        TelemetryConfiguration.getActive().setRoleName("");
    }

    @Test
    void telemetryClient_shouldSetInstrumentationKeyAndRoleName() {
        String instrumentationKey = "test-key";
        String msName = "test-app";
        TelemetryClient client = config.telemetryClient(instrumentationKey, msName);

        assertNotNull(client);
        assertEquals(instrumentationKey, TelemetryConfiguration.getActive().getInstrumentationKey());
        assertEquals(msName, TelemetryConfiguration.getActive().getRoleName());
    }

    @Test
    void gson_shouldSerializeZonedDateTime() {
        Gson gson = config.gson();
        ZonedDateTime now = ZonedDateTime.now();
        String json = gson.toJson(now);
        assertTrue(json.contains(now.toString()));
    }
}
