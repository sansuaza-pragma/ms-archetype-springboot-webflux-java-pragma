package com.mercantil.operationsandexecution.crosscutting.exceptions.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionsConstants {

    /* Constantes Extras de utilidad */
    public static final String UTILITY_EXCEPTION_TYPE = "exceptionType";
    public static final String UTILITY_EXCEPTION_MESSAGE = "exceptionMessage";
    public static final String UTILITY_KEY_NAME_EXCEPTION_TYPE = "exceptionType";
    public static final String UTILITY_KEY_NAME_EXCEPTION_MESSAGE = "exceptionMessage";
    public static final String UTILITY_MESSAGE_URL = "URL";
    public static final String UTILITY_URI_CONSTANT = "uri=";
    public static final String UTILITY_VOID_CONSTANT = "";

    /*  Constantes de errores */
    public static final String EXCEPTION_HANDLER_ERROR = "Unexpected server error preventing request completion.";
    public static final String EXTERNAL_EXCEPTION = "Gateway Timeout - Gateway timed out at the limit set in APIM.";

    /* Constantes de MessageKey */
    public static final String BAD_REQUEST_EXCEPTION_CONSTANT_KEY_INTERNAL = "httpResponse.400.internalMessagePrefix";
    public static final String NOT_FOUND_EXCEPTION_CONSTANT_KEY_INTERNAL = "httpResponse.404.internalMessagePrefix";
    public static final String BODY_REQUIRED_MESSAGE = "Body requerido";

}
