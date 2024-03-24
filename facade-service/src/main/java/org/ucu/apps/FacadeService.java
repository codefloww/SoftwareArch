package org.ucu.apps;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.Random;

@Service
public class FacadeService {
    Logger logger = Logger.getLogger(FacadeService.class.getName());

    private final List<WebClient> loggingClients;

    private final List<WebClient> messagesClients;

    private IQueue<String> messagesQueue =  Hazelcast.newHazelcastInstance().getQueue("messagesQueue");

    private final Random randomGen = new Random();

    public FacadeService() {
        this.loggingClients = List.of(
                WebClient.create("http://localhost:8081"),
                WebClient.create("http://localhost:8082"),
                WebClient.create("http://localhost:8083")
        );
        this.messagesClients = List.of(
                WebClient.create("http://localhost:8084"),
                WebClient.create("http://localhost:8085")
        );
    }

    public Mono<String> facadeGetMessages() {
        WebClient loggingClient = getRandomClient(loggingClients);
        WebClient messagesClient = getRandomClient(messagesClients);

        logger.info("Using logging service for GET: " + loggingClient.toString());
        Mono<String> cachedValues = loggingClient.get()
                .uri("/logging")
                .retrieve()
                .bodyToMono(String.class);

        logger.info("Using messages service for GET: " + messagesClient.toString());
        Mono<String> messageMono = messagesClient.get()
                .uri("/messages")
                .retrieve()
                .bodyToMono(String.class);

        return cachedValues.zipWith(messageMono, (cached, message) -> cached + " " + message)
                .onErrorReturn("Error");
    }

    public Mono<Void> facadePostMessage(@RequestBody String text) throws InterruptedException{
        var msg = new Message(UUID.randomUUID(), text);
        var loggingWebClient = getRandomClient(loggingClients);
        logger.info("Using logging service to POST: " + loggingWebClient.toString());

        // producer of messages for messages service
        addMessageToQueue(text);

        return loggingWebClient.post()
                .uri("/logging")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private void addMessageToQueue(String text) throws InterruptedException {
        messagesQueue.put(text);
        logger.info("Added message to queue: " + text);
    }

    private WebClient getRandomClient(List<WebClient> clients) {
        return clients.get(randomGen.nextInt(clients.size()));
    }
}
