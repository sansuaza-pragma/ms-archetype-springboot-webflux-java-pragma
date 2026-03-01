package com.mercantil.operationsandexecution.crosscutting.restclients.utility;

import com.mercantil.operationsandexecution.crosscutting.restclients.constants.RestClientConstants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;


/**
 * Validates whether a target host is part of an allowlist.
 *
 * @since 1.0
 */
@RequiredArgsConstructor
public class HostAccessValidator {

    private final String[] allowedHosts;

    /**
     * Constructs the validator from a CSV of allowed hosts.
     *
     * @param allowedHostsCsv comma-separated host list
     */
    public HostAccessValidator(String allowedHostsCsv) {
        this.allowedHosts = allowedHostsCsv.split(RestClientConstants.UTILITY_SPLIT_ALLOWED_HOST);
    }

    /**
     * Verifies that the provided host is allowed.
     *
     * @param host hostname extracted from the request URI
     * @return Mono vacío si el host está permitido, Mono con error si no
     */
    public Mono<Void> validateHostAccess(String host) {
        return Mono.just(host)
                .filter(h -> Arrays.stream(allowedHosts)
                        .map(String::trim)
                        .anyMatch(allowed -> allowed.equals(h))
                )
                .switchIfEmpty(Mono.error(new SecurityException(
                        RestClientConstants.UTILITY_SECURITY_HOST_ERROR_MESSAGE.concat(host)
                ))).then();
    }
}
