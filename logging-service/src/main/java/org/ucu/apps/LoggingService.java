package org.ucu.apps;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoggingService {
    private IMap<UUID, String> messages = Hazelcast.newHazelcastInstance().getMap("messages");

    public void logMessage(Message message) {
        messages.put(message.id(), message.text());
    }

    public IMap<UUID, String> getMessages() {
        return messages;
    }
}
