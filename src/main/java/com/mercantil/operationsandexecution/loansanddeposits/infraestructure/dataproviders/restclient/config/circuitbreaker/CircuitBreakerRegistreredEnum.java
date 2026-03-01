package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.dataproviders.restclient.config.circuitbreaker;

import com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker.CbPropertiesExtractor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CircuitBreakerRegistreredEnum {

    DATA_PRO_RETRIEVE_CARD_TRANSACTION("DataProRetrieveCardTransaction", CbPropertiesExtractor.MEDIUM_NON_CRITICAL);

    private final String internalName;
    private final CbPropertiesExtractor profileKey;


    public static CircuitBreakerRegistreredEnum getProfileKeyByName(String name) {
        return Arrays.stream(CircuitBreakerRegistreredEnum.values())
                .filter(e -> e.internalName.equals(name))
                .findFirst()
                .orElse(null);
    }
}

