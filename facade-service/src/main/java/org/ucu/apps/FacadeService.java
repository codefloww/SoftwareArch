package org.ucu.apps;

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

    private final WebClient messagesClient;

    private static final Random randomGen = new Random();

    public FacadeService() {
        this.loggingClients = List.of(
                WebClient.create("http://localhost:8081"),
                WebClient.create("http://localhost:8082"),
                WebClient.create("http://localhost:8083")
        );
        this.messagesClient = WebClient.create("http://localhost:8084");
    }

    public Mono<String> facadeGetMessages() {

        WebClient loggingClient = getRandomClient();

        logger.info("Using logging service for GET: " + loggingClient.toString());
        Mono<String> cachedValues = loggingClient.get()
                .uri("/logging")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> messageMono = messagesClient.get()
                .uri("/messages")
                .retrieve()
                .bodyToMono(String.class);


        return cachedValues.zipWith(messageMono, (cached, message) -> cached + " " + message)
                .onErrorReturn("Error");
    }

    public Mono<Void> facadePostMessage(@RequestBody String text) {

        var msg = new Message(UUID.randomUUID(), text);

        var loggingWebClient = getRandomClient();
        logger.info("Using logging service to POST: " + loggingWebClient.toString());


        return loggingWebClient.post()
                .uri("/logging")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private WebClient getRandomClient() {
        return loggingClients.get(randomGen.nextInt(loggingClients.size()));
    }
}
