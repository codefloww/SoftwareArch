package org.ucu.apps;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class LoggingService {
    private final HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
    private Map<UUID, String> messages = hzInstance.getMap("messages");

    public void logMessage(Message message) {
        messages.put(message.id(), message.text());
    }

    public Map<UUID, String> getMessages() {
        return messages;
    }


}
