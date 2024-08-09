package com.sparta.msa_exam.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthClient {

    @Value("${auth.host}")
    private String authHost;

    private final WebClient webClient;

    public AuthClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<HttpStatus> validateUserExists(String userId) {
        return webClient.get()
                .uri(authHost + "/auth/validate?userId=" + userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));})
                .toBodilessEntity()
                .map(response -> (HttpStatus) response.getStatusCode());
    }
}
