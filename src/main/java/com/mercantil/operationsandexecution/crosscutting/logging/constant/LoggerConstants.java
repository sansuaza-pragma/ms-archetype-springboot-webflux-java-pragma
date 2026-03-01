package com.mercantil.operationsandexecution.crosscutting.logging.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerConstants {

    public static final String EXECUTED_SERVICE = "Action executed ";
    public static final String EXTERNAL_CONSUME_MESSAGE = "External service consumed: ";
    public static final String LOG_ADDITIONAL_DATA = "additionalData ";
    public static final String MESSAGE = "Message ";
    public static final String TRANSACTION_ID_CONSTANT = "TransactionId ";
    public static final String TRANSACTION_ID_NOT_SPECIFIED ="NOT_SPECIFIED";
    public static final String NA_CONSTANT = "N/A";
    public static final String UNKNOWN_ERROR = "Unknown error";
    public static final String TRANSACTION_TYPE_CONSTANT ="TransactionType";
    public static final String OPERATION_URI_CONSTANT = "OperationUri";
    public static final String SERVICE_DOMAIN_CONSTANT = "OperationalGateway";

    public static final String UTILITY_TRANSACTION_ID = "transactionId = ";
    public static final String UTILITY_LOG_ACTION = "Process = ";
    public static final String UTILITY_LOG_INFORMATION = "Information = ";
    public static final String UTILITY_LOG_ADDITIONAL_DATA = "additionalData = ";
    public static final String UTILITY_LOG_SPACE = ", ";
}
