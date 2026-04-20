package org.zvrg.rbacdemo.sevice.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.zvrg.rbacdemo.common.Constants;
import org.zvrg.rbacdemo.sevice.JwtService;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final Environment environment;

    @Override
    public String generateToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                //.claims(Map.of(ROLES, roles))
                .issuedAt(new Date(Instant.now().toEpochMilli()))
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractToken(ServerWebExchange exchange) {
        final var authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(Constants.WebConstants.BEARER)) {
            return authHeader.substring(Constants.WebConstants.BEARER.length()).trim();
        }

        return null;
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.just(token)
                .map(this::parseToken)
                .map(claims -> !claims.getExpiration().before(new Date()))
                .onErrorReturn(false);
    }

    @Override
    public Mono<String> extractSubject(String token) {
        return Mono.just(token)
                .map(this::parseToken)
                .map(Claims::getSubject);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Optional.ofNullable(environment.getProperty("token.secret")) //TODO
                .map(String::getBytes)
                .map(Keys::hmacShaKeyFor)
                .orElseThrow(() -> new IllegalArgumentException("token.secret must be configured in the application properties"));
    }

}
