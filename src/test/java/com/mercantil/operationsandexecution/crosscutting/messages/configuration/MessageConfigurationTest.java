package com.mercantil.operationsandexecution.crosscutting.messages.configuration;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import static com.mercantil.operationsandexecution.crosscutting.messages.constants.MessagesConstants.UTILITY_CACHE_REFRESH_TIME_IN_SECONDS;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageConfigurationTest implements WithAssertions {

    @Mock
    private Environment environment;

    private MessageConfiguration messageConfiguration;

    @BeforeEach
    void setUp() {
        messageConfiguration = new MessageConfiguration(environment);
    }

    @Test
    @DisplayName("Should successfully create and configure ReloadableResourceBundleMessageSource")
    void shouldCreateMessageSourceSuccessfully() {
        // Given
        String expectedCacheSeconds = "3600";
        when(environment.getProperty(UTILITY_CACHE_REFRESH_TIME_IN_SECONDS))
                .thenReturn(expectedCacheSeconds);

        // When
        ReloadableResourceBundleMessageSource result = messageConfiguration.messageSource();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBasenameSet()).containsExactly("file:/app/messages");
    }

    @Test
    @DisplayName("Should throw NumberFormatException when cache refresh time is not a valid integer")
    void shouldThrowExceptionWhenCacheTimeIsInvalid() {
        // Given
        when(environment.getProperty(UTILITY_CACHE_REFRESH_TIME_IN_SECONDS))
                .thenReturn("invalid_number");

        // When / Then
        assertThatThrownBy(() -> messageConfiguration.messageSource())
                .isInstanceOf(NumberFormatException.class);
    }
}