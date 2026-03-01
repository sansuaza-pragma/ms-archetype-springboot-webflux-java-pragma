package com.mercantil.operationsandexecution.crosscutting.utility.label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotype annotation for domain-layer services.
 * <p>
 * Classes annotated with {@code @DomainService} can be selectively picked up by
 * {@link com.mercantil.operationsandexecution.crosscutting.utility.configuration.DomainServiceConfig}
 * and registered as Spring beans, keeping scanning focused and intentional.
 * <p>
 * This annotation is {@link java.lang.annotation.Inherited}, so subclasses of an annotated type inherit it.
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DomainService {
}
