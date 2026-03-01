package com.mercantil.operationsandexecution.crosscutting.entrypointfilter.constant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HandleSecurityConstants {
    public static final String HEADER_TRANSACTION_ID = "transactionId";
    public static final String THREAD_OPERATION_URI = "OperationUri";
    public static final String UTILITY_TRANSACTION_ID_NOT_SPECIFIED = "NOT_SPECIFIED";
    public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    public static final String X_FRAME_OPTIONS = "X-Frame-Options";
    public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    public static final String REFERRER_POLICY = "Referrer-Policy";
    public static final String PERMISSIONS_POLICY = "Permissions-Policy";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String PRAGMA = "Pragma";
    public static final String EXPIRES = "Expires";
    public static final String X_XSS_PROTECTION = "X-XSS-Protection";

    // Valores
    public static final String VALUE_NOSNIFF = "nosniff";
    public static final String VALUE_DENY = "DENY";
    public static final String VALUE_CSP = "default-src 'self'; frame-ancestors 'none'";
    public static final String VALUE_HSTS = "max-age=63072000; includeSubDomains; preload";
    public static final String VALUE_REFERRER = "strict-origin-when-cross-origin";
    public static final String VALUE_PERMISSIONS = "camera=(), geolocation=()";
    public static final String VALUE_NO_CACHE = "no-cache, no-store, must-revalidate";
    public static final String VALUE_PRAGMA = "no-cache";
    public static final String VALUE_EXPIRES = "0";
    public static final String VALUE_XXSS = "1; mode=block";
}
