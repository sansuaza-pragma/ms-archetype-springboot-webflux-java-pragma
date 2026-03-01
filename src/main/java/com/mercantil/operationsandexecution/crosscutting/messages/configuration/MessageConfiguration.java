package com.mercantil.operationsandexecution.crosscutting.messages.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_BASE_NAME;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_CACHE_REFRESH_TIME_IN_SECONDS;
import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_DEFAULT_ENCODING;

/**
 * Spring configuration for i18n messages.
 * <p>
 * Provides a {@link org.springframework.context.support.ReloadableResourceBundleMessageSource} with configurable cache timeout
 * and default encoding.
 *
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
public class MessageConfiguration {

    private final Environment environment;

    /**
     * Declares the message source used by the application.
     *
     * @return configured {@link org.springframework.context.support.ReloadableResourceBundleMessageSource}
     * @throws NumberFormatException if cache seconds is not a valid integer
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        String cacheRefresshTime = environment.getProperty(UTILITY_CACHE_REFRESH_TIME_IN_SECONDS);
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(UTILITY_BASE_NAME);
        messageSource.setDefaultEncoding(UTILITY_DEFAULT_ENCODING);
        messageSource.setCacheSeconds(Integer.parseInt(cacheRefresshTime));
        return messageSource;
    }
}
