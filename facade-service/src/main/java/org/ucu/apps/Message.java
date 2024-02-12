package org.ucu.apps;

import java.util.UUID;

public record Message(UUID id, String text) {
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
