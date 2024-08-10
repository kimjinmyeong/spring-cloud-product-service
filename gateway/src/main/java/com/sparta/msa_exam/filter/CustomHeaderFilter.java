package com.sparta.msa_exam.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomHeaderFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        String backendServerPort = exchange.getResponse().getHeaders().getFirst("Server-Port");
        if (backendServerPort != null) {
            headers.add("Server-Port", backendServerPort);
        }
        return chain.filter(exchange);
    }
}
