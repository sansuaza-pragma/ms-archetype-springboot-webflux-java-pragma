package com.mercantil.operationsandexecution.crosscutting.messages.implementation;

import com.mercantil.operationsandexecution.crosscutting.entrypointfilter.ConstEntryPointRest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Locale;

import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_GENERIC_ERROR_MESSAGE;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_HTTP_RESPONSE_LITERAL;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_HTTP_RESPONSE_MAP;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_TRANSACTION_ID_NOT_SPECIFIED;


/**
 * Implementación por defecto de {@link IMessageService} basada en {@link org.springframework.context.MessageSource} de Spring.
 * Proporciona métodos para obtener mensajes internacionalizados y datos de la petición HTTP
 * de forma reactiva y resiliente.
 * Utiliza programación reactiva (Mono) y maneja operaciones bloqueantes con Schedulers.boundedElastic().
 *
 * @since 1.0
 */
@RequiredArgsConstructor
@Service
public class MessageService implements IMessageService {

    private final MessageSource messageSource;


    /**
     * Obtiene un mensaje internacionalizado por clave.
     * Si ocurre un error, retorna la clave como mensaje.
     *
     * @param key Clave del mensaje.
     * @return Mono con el mensaje localizado o la clave si hay error.
     */
    @Override
    public Mono<String> getMessage(String key) {
        return Mono.fromCallable(() ->
                messageSource.getMessage(key, null, Locale.getDefault())
        ).onErrorReturn(key);
    }

    /**
     * Obtiene un mensaje asociado a un código HTTP.
     * Utiliza un scheduler elástico para operaciones bloqueantes.
     *
     * @param statusCode Código HTTP.
     * @return Mono con el mensaje localizado.
     */
    @Override
    public Mono<String> getMessageByHttpStatus(HttpStatusCode statusCode) {
        return Mono.fromCallable(() ->
                        messageSource.getMessage(UTILITY_HTTP_RESPONSE_MAP + statusCode.value(), null, Locale.getDefault())
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Obtiene el identificador de transacción desde los headers de la petición.
     * Si no existe, retorna un mensaje por defecto.
     *
     * @param exchange Contexto de la petición HTTP.
     * @return Mono con el identificador de transacción o mensaje por defecto.
     */
    @Override
    public Mono<String> getTransactionIdLog(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID))
                .defaultIfEmpty(UTILITY_TRANSACTION_ID_NOT_SPECIFIED);
    }

    /**
     * Obtiene un mensaje asociado a un código HTTP en formato String.
     * Si ocurre un error, retorna un mensaje genérico.
     * Utiliza un scheduler elástico para operaciones bloqueantes.
     *
     * @param statusCode Código HTTP como String.
     * @return Mono con el mensaje localizado o mensaje genérico si hay error.
     */
    @Override
    public Mono<String> getHttpStatusCodeMessage(String statusCode) {
        return Mono.fromCallable(() ->
                        messageSource.getMessage(UTILITY_HTTP_RESPONSE_LITERAL.concat(statusCode), null, Locale.getDefault())
                )
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(error -> getMessage(UTILITY_GENERIC_ERROR_MESSAGE));
    }
}