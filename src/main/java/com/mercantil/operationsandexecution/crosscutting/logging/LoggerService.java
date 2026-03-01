package com.mercantil.operationsandexecution.crosscutting.logging;


import com.google.gson.Gson;
import com.mercantil.operationsandexecution.crosscutting.logging.model.ContextDataKey;
import com.mercantil.operationsandexecution.crosscutting.logging.model.MetaData;
import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.SeverityLevel;
import com.microsoft.applicationinsights.telemetry.TraceTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.EXECUTED_SERVICE;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.EXTERNAL_CONSUME_MESSAGE;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.LOG_ADDITIONAL_DATA;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.MESSAGE;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.NA_CONSTANT;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.OPERATION_URI_CONSTANT;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.SERVICE_DOMAIN_CONSTANT;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.TRANSACTION_ID_CONSTANT;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.TRANSACTION_ID_NOT_SPECIFIED;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.TRANSACTION_TYPE_CONSTANT;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.UNKNOWN_ERROR;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.UTILITY_LOG_ACTION;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.UTILITY_LOG_ADDITIONAL_DATA;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.UTILITY_LOG_INFORMATION;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.UTILITY_LOG_SPACE;
import static com.mercantil.operationsandexecution.crosscutting.logging.constant.LoggerConstants.UTILITY_TRANSACTION_ID;


/**
 * Servicio de logging reactivo que integra Application Insights y log4j2.
 * Permite registrar trazas, eventos, advertencias y errores con metadatos de contexto.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class LoggerService implements ILoggerService {
    private final TelemetryClient telemetryClient;
    private final Gson gson;

    /**
     * Registra un log de tipo TRACE con información adicional.
     *
     * @param enumMsAction   Acción o evento a registrar.
     * @param message        Mensaje descriptivo.
     * @param additionalData Datos adicionales para el log.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    @Override
    public Mono<Void> logTraceInfo(EnumActionLogs enumMsAction, String message, Object additionalData) {
        return logTraceTelemetry(enumMsAction, message, gson.toJson(additionalData));
    }

    /**
     * Registra el consumo de un servicio externo.
     *
     * @param enumMsAction  Acción o evento a registrar.
     * @param url           URL del servicio externo.
     * @param sanitizedBody Cuerpo de la petición/response (sanitizado).
     * @return Mono que completa cuando el log ha sido procesado.
     */
    @Override
    public Mono<Void> logExternalConsume(EnumActionLogs enumMsAction, String url, Object sanitizedBody) {
        String externalConsumeMessage = EXTERNAL_CONSUME_MESSAGE.concat(url);
        return logTraceTelemetry(enumMsAction, externalConsumeMessage, gson.toJson(sanitizedBody));
    }

    /**
     * Registra un evento personalizado.
     *
     * @param msAction       Acción o evento a registrar.
     * @param eventName      Nombre del evento.
     * @param message        Mensaje descriptivo.
     * @param additionalData Datos adicionales para el log.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    @Override
    public Mono<Void> logEvent(EnumActionLogs msAction, String eventName, String message, String additionalData) {
        return logEventTelemetry(msAction, eventName, message, additionalData);
    }

    /**
     * Registra un mensaje de depuración.
     *
     * @param enumMsAction   Acción o evento a registrar.
     * @param message        Mensaje descriptivo.
     * @param additionalData Datos adicionales para el log.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    @Override
    public Mono<Void> logDebug(EnumActionLogs enumMsAction, String message, Object additionalData) {
        return Mono.fromRunnable(() -> log.log(Level.DEBUG, formatLog(TRANSACTION_ID_NOT_SPECIFIED, enumMsAction,
                message, gson.toJson(additionalData)))).then();
    }

    /**
     * Registra una advertencia.
     *
     * @param enumMsAction Acción o evento a registrar.
     * @param throwable    Excepción o advertencia.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    @Override
    public Mono<Void> logWarning(EnumActionLogs enumMsAction, Throwable throwable) {
        return logExceptionTelemetry(enumMsAction, throwable, gson.toJson(throwable.getMessage()), Level.WARN);
    }

    /**
     * Registra un error.
     *
     * @param enumMsAction Acción o evento a registrar.
     * @param throwable    Excepción o error ocurrido.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    @Override
    public Mono<Void> logError(EnumActionLogs enumMsAction, Throwable throwable, Object additionalData) {
        return logExceptionTelemetry(enumMsAction, throwable, gson.toJson(additionalData), Level.ERROR);
    }

    /**
     * Registra un mensaje en log4j2 con el nivel especificado.
     *
     * @param type             Nivel de log.
     * @param messageFormatted Mensaje ya formateado.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    private Mono<Void> logWithType(Level type, String messageFormatted) {
        return Mono.fromRunnable(() -> log.log(type, messageFormatted));
    }

    /**
     * Registra una traza en Application Insights y log4j2.
     */
    private Mono<Void> logTraceTelemetry(EnumActionLogs enumMsAction, String message, String additionalData) {
        var data = additionalData != null ? gson.toJson(additionalData) : NA_CONSTANT;
        return Mono.deferContextual(contextView -> {
            MetaData metaData = contextView.get(ContextDataKey.META_DATA);
            return Mono.fromCallable(() -> {
                TraceTelemetry trace = new TraceTelemetry();
                String messageFormatted = formatLog(metaData.getTransactionId(),
                        enumMsAction, message, data);
                trace.setMessage(formatLog(metaData.getTransactionId(),
                        enumMsAction, messageFormatted, data));
                trace.setSeverityLevel(SeverityLevel.Information);
                trace.getProperties().put(TRANSACTION_ID_CONSTANT, metaData.getTransactionId());
                trace.getProperties().put(OPERATION_URI_CONSTANT, metaData.getEndpoint());
                trace.getProperties().put(TRANSACTION_TYPE_CONSTANT, SERVICE_DOMAIN_CONSTANT);
                telemetryClient.trackTrace(trace);
                return messageFormatted;
            }).flatMap(messageFormatted -> logWithType(Level.INFO, messageFormatted));
        });
    }

    /**
     * Registra un evento personalizado en Application Insights y log4j2.
     */
    private Mono<Void> logEventTelemetry(EnumActionLogs enumMsAction, String eventName, String message, String additionalData) {
        String data = additionalData != null ? gson.toJson(additionalData) : NA_CONSTANT;
        return Mono.deferContextual(contextView ->
                Mono.fromCallable(() -> {
                    MetaData metaData = contextView.get(ContextDataKey.META_DATA);
                    EventTelemetry event = new EventTelemetry(eventName);
                    populatePropsEventAndException(event.getProperties(), metaData.getTransactionId(),
                            enumMsAction, data, message, metaData.getEndpoint());
                    telemetryClient.trackEvent(event);
                    return metaData.getTransactionId();
                }).flatMap(transactionId -> logWithType(Level.INFO, message))
        );
    }

    /**
     * Registra una excepción en Application Insights y log4j2.
     */
    private Mono<Void> logExceptionTelemetry(EnumActionLogs enumMsAction, Throwable throwable, String additionalData, Level level) {
        String data = additionalData != null ? gson.toJson(additionalData) : NA_CONSTANT;
        return Mono.deferContextual(contextView -> {
            MetaData metaData = contextView.get(ContextDataKey.META_DATA);
            ExceptionTelemetry exception = new ExceptionTelemetry(throwable);
            populatePropsEventAndException(exception.getProperties(), metaData.getTransactionId(),
                    enumMsAction, data, throwable.getLocalizedMessage(), metaData.getEndpoint());
            exception.setSeverityLevel(SeverityLevel.Error);
            telemetryClient.trackException(exception);
            log.error(formatLog(metaData.getTransactionId(), enumMsAction, throwable.getMessage(), additionalData));
            return Mono.just(throwable.getMessage() != null ? throwable.getMessage() : UNKNOWN_ERROR);
        }).flatMap(errorMessage -> logWithType(level, errorMessage));
    }

    /**
     * Agrega propiedades comunes a los eventos y excepciones de Application Insights.
     */
    private void populatePropsEventAndException(Map<String, String> properties, String transactionId,
                                                EnumActionLogs action, Object additionalData, String message,
                                                String endpoint) {
        properties.put(TRANSACTION_ID_CONSTANT, transactionId);
        properties.put(EXECUTED_SERVICE, action.getLogString());
        properties.put(MESSAGE, message);
        properties.put(LOG_ADDITIONAL_DATA, additionalData.toString());
        properties.put(OPERATION_URI_CONSTANT, endpoint);
        properties.put(TRANSACTION_TYPE_CONSTANT, SERVICE_DOMAIN_CONSTANT);
    }

    /**
     * Construye el mensaje de fin de proceso exitoso.
     */

    private String formatLog(String transactionId, EnumActionLogs action, String message, Object additionalData) {
        String transactionIdValue = StringUtils.isNotEmpty(transactionId) ? transactionId
                : TRANSACTION_ID_NOT_SPECIFIED;
        StringBuilder logMessage = new StringBuilder()
                .append(UTILITY_TRANSACTION_ID).append(transactionIdValue).append(UTILITY_LOG_SPACE)
                .append(UTILITY_LOG_ACTION).append(action.getLogString()).append(UTILITY_LOG_SPACE)
                .append(UTILITY_LOG_INFORMATION).append(message);

        if (ObjectUtils.isNotEmpty(additionalData)) {
            logMessage.append(UTILITY_LOG_SPACE).append(UTILITY_LOG_ADDITIONAL_DATA).append(additionalData);
        }

        return logMessage.toString();
    }

}