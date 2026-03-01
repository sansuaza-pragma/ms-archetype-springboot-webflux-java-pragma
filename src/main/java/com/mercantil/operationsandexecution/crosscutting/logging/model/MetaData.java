package com.mercantil.operationsandexecution.crosscutting.logging.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MetaData {
    private final String transactionId;
    private final String endpoint;
}
