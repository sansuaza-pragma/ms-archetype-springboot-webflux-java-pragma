package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.entrypoints.rest.validationhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidatedRequest<T, M> {
    private T body;
    private M metadata;

    public static <T, M> ValidatedRequest<T, M> of(T body, M metadata) {
        return new ValidatedRequest<>(body, metadata);
    }

}