package com.mercantil.operationsandexecution.crosscutting.restclients.models.circuitbreaker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo que encapsula las propiedades configurables de una instancia de circuit breaker.
 * Permite definir umbrales y parámetros de operación para el control de fallos y ventanas de llamadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceProperties {
    /** Porcentaje de fallos permitido antes de abrir el circuito. */
    private int failureRateThreshold;
    /** Duración (en segundos o milisegundos) que el circuito permanece abierto antes de intentar reabrir. */
    private int waitDurationInOpenState;
    /** Número de llamadas permitidas en estado half-open para probar la recuperación. */
    private int permittedNumberOfCallsInHalfOpenState;
    /** Número mínimo de llamadas requeridas para calcular el porcentaje de fallos. */
    private int minimumNumberOfCalls;
    /** Tamaño de la ventana deslizante para el cálculo de métricas. */
    private int slidingWindowSize;
}