package org.ucu.apps;

import com.hazelcast.map.IMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class LoggingController {
    Logger logger = Logger.getLogger(LoggingController.class.getName());

    private final LoggingService loggingService;

    public LoggingController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }


    @GetMapping("/logging")
    public String getMessages() {
        IMap<UUID, String> messages = loggingService.getMessages();
        return messages.values().toString();
    }

    @PostMapping("/logging")
    public ResponseEntity<Void> logMessage(@RequestBody Message message) {
        if (message == null) {
            logger.log(Level.WARNING, "Received null message");
            return ResponseEntity.badRequest().build();
        } else {
            logger.info("Received message: " + message.text());
            loggingService.logMessage(message);
            return ResponseEntity.ok().build();
        }
    }
}
