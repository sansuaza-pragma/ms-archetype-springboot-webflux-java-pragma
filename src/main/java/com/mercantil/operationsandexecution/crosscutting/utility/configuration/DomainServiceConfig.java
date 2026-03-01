package com.mercantil.operationsandexecution.crosscutting.utility.configuration;

import com.mercantil.operationsandexecution.crosscutting.utility.label.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Spring configuration that scans the project for beans annotated with {@link com.mercantil.operationsandexecution.crosscutting.utility.label.DomainService}
 * and registers only those as Spring components.
 * <p>
 * This allows you to mark pure domain-layer services explicitly and keep the container
 * free of unintended beans by disabling default filters.
 *
 * @since 1.0
 */
@Configuration
@ComponentScan(
        basePackages = "com.mercantil",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = DomainService.class),
        useDefaultFilters = false
)
public class DomainServiceConfig {
}
