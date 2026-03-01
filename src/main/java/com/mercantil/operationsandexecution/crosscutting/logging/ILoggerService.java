package com.mercantil.operationsandexecution.crosscutting.logging;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.EnumActionLogs;
import reactor.core.publisher.Mono;

/**
 * Interfaz para servicios de logging reactivo.
 * Permite registrar diferentes tipos de eventos y mensajes en la aplicación.
 */
public interface ILoggerService {

    /**
     * Registra un log de tipo TRACE con información adicional.
     *
     * @param enumMsAction   Acción o evento a registrar.
     * @param message        Mensaje descriptivo.
     * @param additionalData Datos adicionales para el log.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    Mono<Void> logTraceInfo(EnumActionLogs enumMsAction, String message, Object additionalData);

    /**
     * Registra el consumo de un servicio externo.
     *
     * @param enumMsAction  Acción o evento a registrar.
     * @param url           URL del servicio externo.
     * @param sanitizedBody Cuerpo de la petición/response (sanitizado).
     * @return Mono que completa cuando el log ha sido procesado.
     */
    Mono<Void> logExternalConsume(EnumActionLogs enumMsAction, String url, Object sanitizedBody);

    /**
     * Registra un evento personalizado.
     *
     * @param msAction       Acción o evento a registrar.
     * @param eventName      Nombre del evento.
     * @param message        Mensaje descriptivo.
     * @param additionalData Datos adicionales para el log.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    Mono<Void> logEvent(EnumActionLogs msAction, String eventName, String message, String additionalData);

    /**
     * Registra un error.
     *
     * @param enumMsAction Acción o evento a registrar.
     * @param throwable    Excepción o error ocurrido.
     * @param additionalData Datos adicionales para el log.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    Mono<Void> logError(EnumActionLogs enumMsAction, Throwable throwable, Object additionalData);

    /**
     * Registra una advertencia.
     *
     * @param enumMsAction Acción o evento a registrar.
     * @param throwable    Excepción o advertencia.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    Mono<Void> logWarning(EnumActionLogs enumMsAction, Throwable throwable);

    /**
     * Registra un mensaje de depuración.
     *
     * @param enumMsAction   Acción o evento a registrar.
     * @param message        Mensaje descriptivo.
     * @param additionalData Datos adicionales para el log.
     * @return Mono que completa cuando el log ha sido procesado.
     */
    Mono<Void> logDebug(EnumActionLogs enumMsAction, String message, Object additionalData);
}