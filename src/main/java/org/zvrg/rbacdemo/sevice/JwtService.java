package org.zvrg.rbacdemo.sevice;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JwtService {

    //TODO
    String generateToken(UUID userId);

    //TODO
    String extractToken(ServerWebExchange exchange);

    Mono<Boolean> validateToken(String token);

    Mono<String> extractSubject(String token);

}
