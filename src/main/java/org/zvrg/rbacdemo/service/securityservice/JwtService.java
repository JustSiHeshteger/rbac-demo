package org.zvrg.rbacdemo.service.securityservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.ClockProvider;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.zvrg.rbacdemo.common.Constants;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final ClockProvider clockProvider;
    private final SecretKey signingKey;
    private final Long expirationHours;

    public JwtService(@Value("${token.secret}") String secret,
                      @Value("${token.expiration-hours:1}") Long expirationHours,
                      ClockProvider clockProvider) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationHours = expirationHours;
        this.clockProvider = clockProvider;
    }

    public Mono<String> generateToken(UUID userId) {
        return Mono.fromCallable(() -> {
            final var now = clockProvider.getClock().instant();
            return Jwts.builder()
                            .subject(userId.toString())
                            .issuedAt(Date.from(now))
                            .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
                            .signWith(signingKey)
                            .compact();
            });
    }

    public Mono<String> extractToken(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> Strings.CS.startsWith(authHeader, Constants.WebConstants.BEARER))
                .map(authHeader -> Strings.CS.removeStart(authHeader, Constants.WebConstants.BEARER));
    }

    public Mono<Boolean> validateToken(String token) {
        return Mono.just(token)
                .flatMap(this::parseToken)
                .map(_ -> true)
                .onErrorReturn(false);
    }

    public Mono<String> extractSubject(String token) {
        return Mono.just(token)
                .flatMap(this::parseToken)
                .map(Claims::getSubject);
    }

    private Mono<Claims> parseToken(String token) {
        return Mono.fromCallable(() -> Jwts.parser()
                .verifyWith(signingKey)
                .clock(() -> Date.from(clockProvider.getClock().instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
        );
    }

}
