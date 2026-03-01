package com.mercantil.operationsandexecution.crosscutting.documentation.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SwaggerConstants {

    // Configuración OpenAPI principal
    public static final String UTILITY_OPEN_API_GROUP = "loan";
    public static final String UTILITY_OPEN_API_PATHS_TO_MATCH = "/loan/**";
    public static final String UTILITY_OPEN_API_PATHS_TO_MATCH_ORIGINAL = "/loan/v1";
    public static final String UTILITY_OPEN_API_INFO_TITLE = "Loan flow for the Java archetype - Mercantil";
    public static final String UTILITY_OPEN_API_INFO_DESCRIPTION = "API for loan orchestration in the archetype";
    public static final String UTILITY_OPEN_API_INFO_VERSION = "1.0.0";
    public static final String UTILITY_OPEN_API_INFO_CONTACT_NAME = "Vicepresidencia de Tecnología y Digital - Mercantil Banco";
    public static final String UTILITY_OPEN_API_INFO_CONTACT_URL = "https://mercantilbanco.com.pa/";
    public static final String UTILITY_OPEN_API_INFO_CONTACT_EMAIL = "vicepresidenciadetecnologiaydigital@mercantilbanco.com.pa";
    public static final String UTILITY_OPEN_API_INFO_TERMS_OF_SERVICE = "https://mercantilbanco.com.pa/";
    public static final String UTILITY_OPEN_API_INFO_LICENCE_NAME = "Apache 2.0";
    public static final String UTILITY_OPEN_API_INFO_LICENCE_URL = "https://www.apache.org/licenses/LICENSE-2.0.html";
    public static final String UTILITY_OPEN_API_SCAN_PACKAGE = "com.mercantil.operationsandexecution";
    public static final String UTILITY_OPEN_API_SERVER_URL_APIM = "https://nova-apim-gw.qa.mercantilbanco.local/loan/v1";
    public static final String UTILITY_OPEN_API_SERVER_URL_GATEWAY = "https://qa-nova-gw.mercantilbanco.com.pa/loan/v1";
    public static final String UTILITY_OPEN_API_SERVER_DESCRIPTION_APIM = "URL for APIM testing";
    public static final String UTILITY_OPEN_API_SERVER_DESCRIPTION_GATEWAY = "URL for Gateway testing";

}
