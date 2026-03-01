package com.mercantil.operationsandexecution.crosscutting.entrypointfilter;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConstEntryPointRestTest implements WithAssertions {

    @Test
    void shouldContainExpectedTransactionIdHeaderKey() {
        assertThat(ConstEntryPointRest.HeaderKeys.TRANSACTION_ID)
                .isEqualTo("transactionId");
    }
}