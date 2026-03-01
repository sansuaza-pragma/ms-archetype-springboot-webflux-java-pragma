package com.mercantil.operationsandexecution.loansanddeposits.infraestructure.entrypoints.rest.router.model;

import com.mercantil.operationsandexecution.loansanddeposits.infraestructure.entrypoints.rest.model.request.CreateLoanBodyReq;

import java.math.BigDecimal;

public class CreateLoanReqMother {
    public static CreateLoanBodyReq build(){
        return new CreateLoanBodyReq("Roberto Carlos", "HIPOTECARIO",
                new BigDecimal(1000), 1.4);
    }

    public static String buildJsonResponse(String date) {
        return "{"
                + "\"statusCode\":\"200\","
                + "\"status\":\"OK\","
                + "\"message\":\"OK - The request was successfully executed.\","
                + "\"timestamp\":\"" + date + "\","
                + "\"transactionId\":\"" + EntryPointConstantTests.TRANSACTION_ID_VALUE + "\""
                + "}";
    }
}
