package com.mercantil.operationsandexecution.crosscutting.logging;

import com.google.gson.Gson;
import com.mercantil.operationsandexecution.crosscutting.logging.model.ContextDataKey;
import com.mercantil.operationsandexecution.crosscutting.logging.model.MetaData;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.TraceTelemetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LoggerServiceTest {

    private final String tranasactionId = "TX-123";
    private TelemetryClient telemetryClient;
    private Gson gson;
    private LoggerService loggerService;
    private MetaData metaData;

    @BeforeEach
    void setUp() {
        telemetryClient = mock(TelemetryClient.class);
        gson = new Gson();
        loggerService = new LoggerService(telemetryClient, gson);
        metaData = new MetaData(tranasactionId, "/test-endpoint");
    }

    @Test
    void logTraceInfo_shouldLogTraceTelemetry() {
        Mono<Void> result = loggerService.logTraceInfo(EnumActionLogs.NOT_IDENTIFIED, "test-message", "data")
                .contextWrite(Context.of(ContextDataKey.META_DATA, metaData));
        StepVerifier.create(result).verifyComplete();
        verify(telemetryClient, atLeastOnce()).trackTrace(any(TraceTelemetry.class));
    }

    @Test
    void logExternalConsume_shouldLogTraceTelemetry() {
        Mono<Void> result = loggerService.logExternalConsume(EnumActionLogs.NOT_IDENTIFIED, "http://test", "body")
                .contextWrite(Context.of(ContextDataKey.META_DATA, metaData));
        StepVerifier.create(result).verifyComplete();
        verify(telemetryClient, atLeastOnce()).trackTrace(any(TraceTelemetry.class));
    }

    @Test
    void logEvent_shouldLogEventTelemetry() {
        Mono<Void> result = loggerService.logEvent(EnumActionLogs.NOT_IDENTIFIED, "eventName", "msg", "addData")
                .contextWrite(Context.of(ContextDataKey.META_DATA, metaData));
        StepVerifier.create(result).verifyComplete();
        verify(telemetryClient, atLeastOnce()).trackEvent(any(EventTelemetry.class));
    }

    @Test
    void logDebug_shouldLogDebug() {
        Mono<Void> result = loggerService.logDebug(EnumActionLogs.NOT_IDENTIFIED, "debug-msg", "addData");
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void logWarning_shouldLogExceptionTelemetry() {
        Throwable t = new RuntimeException("warn");
        Mono<Void> result = loggerService.logWarning(EnumActionLogs.NOT_IDENTIFIED, t)
                .contextWrite(Context.of(ContextDataKey.META_DATA, metaData));
        StepVerifier.create(result).verifyComplete();
        verify(telemetryClient, atLeastOnce()).trackException(any(ExceptionTelemetry.class));
    }

    @Test
    void logError_shouldLogExceptionTelemetry() {
        Throwable t = new RuntimeException("error");
        Mono<Void> result = loggerService.logError(EnumActionLogs.NOT_IDENTIFIED, t, false)
                .contextWrite(Context.of(ContextDataKey.META_DATA, metaData));
        StepVerifier.create(result).verifyComplete();
        verify(telemetryClient, atLeastOnce()).trackException(any(ExceptionTelemetry.class));
    }


}
