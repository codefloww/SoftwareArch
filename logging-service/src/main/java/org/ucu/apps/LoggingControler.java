package org.ucu.apps;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class LoggingControler {
    Logger logger = Logger.getLogger(LoggingControler.class.getName());

    private final Map<UUID, String> messages = new ConcurrentHashMap<>();


    @GetMapping("/logging")
    public String getMessagesTexts() {
        return messages.values().toString();
    }

    @PostMapping("/logging")
    public ResponseEntity<Void> logMessage(@RequestBody Message message) {
        if (message == null) {
            logger.log(Level.WARNING, "Received null message");
            return ResponseEntity.badRequest().build();
        }else {
            logger.info("Received message: " + message);
            messages.put(message.id(), message.text());
            return ResponseEntity.ok().build();
        }
    }
}
