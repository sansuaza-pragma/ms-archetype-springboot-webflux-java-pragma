package com.mercantil.operationsandexecution.crosscutting.restclients.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Immutable configuration holder for an HTTP request.
 *
 * @param <T> request body type
 * @since 1.0
 */
@Getter
@ToString
@AllArgsConstructor
public class HttpRequestConfiguration<T, R, E> {

    private final HttpMethod method;
    private final Map<String, String> headers;
    private final E requestBody;
    private final EnumActionLogs action;
    private final HttpResponsePayload<T, R> httpResponsePayload;

    @Getter
    @ToString
    @AllArgsConstructor
    public static class HttpResponsePayload<T, R> {
        private final Class<T> responseType;
        private final Class<R> errorResponseType;
    }
}
