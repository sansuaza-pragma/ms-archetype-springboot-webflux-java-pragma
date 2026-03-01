package com.mercantil.operationsandexecution.crosscutting.messages.implementation;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Interface reactiva para la gestión de mensajes y localización en aplicaciones WebFlux.
 * Proporciona métodos para obtener mensajes internacionalizados, descripciones de estados HTTP
 * y extraer información relevante de las peticiones HTTP de forma asíncrona y resiliente
 * Todos los métodos retornan un {@link reactor.core.publisher.Mono} para facilitar la integración con flujos reactivos.
 */
public interface IMessageService {

    /**
     * Obtiene un mensaje basado en su clave de forma asíncrona.
     *
     * @param key Identificador del mensaje.
     * @return Mono con el mensaje traducido o la clave si no existe.
     */
    Mono<String> getMessage(String key);

    /**
     * Obtiene el mensaje asociado a un código de estado HTTP.
     *
     * @param statusCode Código de estado.
     * @return Mono con la descripción del estado.
     */
    Mono<String> getMessageByHttpStatus(HttpStatusCode statusCode);

    /**
     * Extrae el ID de transacción de las cabeceras de la petición reactiva.
     *
     * @param exchange Contexto de la petición actual.
     * @return Mono con el ID o un valor por defecto.
     */
    Mono<String> getTransactionIdLog(ServerWebExchange exchange);

    /**
     * Obtiene un mensaje basado en un literal de código de estado.
     *
     * @param statusCode String del código.
     * @return Mono con el mensaje o mensaje de error genérico.
     */
    Mono<String> getHttpStatusCodeMessage(String statusCode);
}