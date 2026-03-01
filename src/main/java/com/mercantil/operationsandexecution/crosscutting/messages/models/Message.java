package com.mercantil.operationsandexecution.crosscutting.messages.models;

import com.mercantil.operationsandexecution.crosscutting.messages.enums.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Simple message envelope with type, content and timestamp.
 *
 * @param <T> content payload type
 * @since 1.0
 */
@Data
public class Message<T> {

    private final LocalDateTime timestamp;
    private MessageType type;
    private T content;

    /**
     * Creates a new message with the current timestamp.
     *
     * @param type    message type (INFO/WARNING/ERROR/SUCCESS)
     * @param content message payload
     */
    public Message(MessageType type, T content) {
        this.type = type;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}
