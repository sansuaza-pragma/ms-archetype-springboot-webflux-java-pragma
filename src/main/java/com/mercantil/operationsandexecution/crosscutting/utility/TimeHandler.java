package com.mercantil.operationsandexecution.crosscutting.utility;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static com.mercantil.operationsandexecution.crosscutting.utility.constants.UtilityConstants.DATE_FORMAT;
import static com.mercantil.operationsandexecution.crosscutting.utility.constants.UtilityConstants.DATE_OFFSET;

/**
 * Time utilities for producing formatted timestamps with a configured UTC offset.
 *
 * @since 1.0
 */
@UtilityClass
public class TimeHandler {

    /**
     * Formatter built from the {@code DATE_FORMAT} pattern (externalized in constants).
     */
    static DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .appendPattern(DATE_FORMAT)
            .toFormatter();

    /**
     * Returns the current date/time formatted using {@link #dateTimeFormatter} and
     * the zone offset defined by {@code DATE_OFFSET}.
     *
     * @return formatted current timestamp
     */
    public static String currentDate() {
        var date = LocalDateTime.now(ZoneOffset.of(DATE_OFFSET));
        return date.format(dateTimeFormatter);
    }
}
