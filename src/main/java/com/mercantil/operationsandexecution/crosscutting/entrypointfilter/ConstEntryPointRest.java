package com.mercantil.operationsandexecution.crosscutting.entrypointfilter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstEntryPointRest {

    @UtilityClass
    public static class HeaderKeys {
        public static final String TRANSACTION_ID = "transactionId";
    }
}
