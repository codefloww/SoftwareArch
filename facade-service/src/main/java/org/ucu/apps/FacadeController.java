package org.ucu.apps;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class FacadeController {
    WebClient loggingWebClient = WebClient.create("http://localhost:8081");
    WebClient messagesWebClient = WebClient.create("http://localhost:8082");

    @GetMapping("/facade_service")
    public Mono<String> facadeGetMessages() {
        Mono<String> cachedValues = loggingWebClient.get()
                .uri("/logging")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> messageMono = messagesWebClient.get()
                .uri("/messages")
                .retrieve()
                .bodyToMono(String.class);

        return cachedValues.zipWith(messageMono, (cached, message) -> cached + " " + message)
                .onErrorReturn("Error");
    }

    @PostMapping("/facade_service")
    public Mono<Void> facadePostMessage(@RequestBody String text) {

        var msg = new Message(UUID.randomUUID(), text);

        return loggingWebClient.post()
                .uri("/logging")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
