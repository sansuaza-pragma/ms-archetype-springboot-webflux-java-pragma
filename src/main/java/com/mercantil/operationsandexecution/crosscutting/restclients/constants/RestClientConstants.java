package com.mercantil.operationsandexecution.crosscutting.restclients.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RestClientConstants {
    /* Constantes Extras de utilidad */
    public static final String UTILITY_SECURITY_HOST_ERROR_MESSAGE = "Blocked request to disallowed host: ";
    public static final String UTILITY_SPLIT_ALLOWED_HOST = ",";

    public static final String EXTERNAL_EXCEPTION = "Gateway Timeout - Gateway timed out at the limit set in APIM.";


    //CB
    public static final String CB_OPEN_CODE = "Circuit Breaker status open due to unavailability of service";
    public static final String SERVER_TOO_SLOW_MESSAGE = "SERVER_TOO_SLOW";
    public static final String LOG_CONSUME_WITH_CIRCUIT_BREAKER = "External service call is being handled through Circuit Breaker: %s - State: %s. ExternalService: %s";
    public static final String EXTERNAL_SERVICE_UNAVAILABLE = "Service temporarily unavailable for: ";
    public static final String PROFILES_LOADED_SUCCESS = "Resilience profiles loaded successfully from configuration file: {}";
    public static final String PROFILES_LOAD_ERROR = "Failed to load resilience profiles from configuration file";
    public static final String USING_DEFAULT_PROFILES = "Fallback to default resilience profiles";
    public static final String YAML_EMPTY_OR_INVALID = "The YAML file is empty or has an invalid format";
    public static final String CIRCUIT_BREAKER_RELOADED = "Circuit breaker [{}] updated with profile [{}] and configuration: {}";

}
