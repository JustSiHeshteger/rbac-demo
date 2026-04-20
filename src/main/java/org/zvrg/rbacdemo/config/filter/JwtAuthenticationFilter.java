package org.zvrg.rbacdemo.config.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.zvrg.rbacdemo.sevice.JwtService;
import org.zvrg.rbacdemo.sevice.RbacService;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final RbacService rbacService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final var token = jwtService.extractToken(exchange);

        if (Objects.isNull(token)) {
            return chain.filter(exchange);
        }

        return jwtService.validateToken(token)
                .flatMap(isValid -> isValid
                        ? authenticateAndContinue(token, exchange, chain)
                        : handleInvalidToken(exchange)
                )
                .onErrorResume(e -> handleInvalidToken(exchange));
    }

    private Mono<? extends Void> authenticateAndContinue(String token,
                                                         ServerWebExchange exchange,
                                                         WebFilterChain chain) {
        return jwtService.extractSubject(token)
                .map(UUID::fromString)
                .flatMap(userId ->
                    // TODO для извлечения ролей сделать кэш
                    // TODO сделать refresh токен
                    rbacService.getAuthoritiesByUserId(userId)
                            .collectList()
                            .flatMap(auth -> {
                                final Authentication authentication =
                                        new UsernamePasswordAuthenticationToken(userId,null, auth);
                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                            })
                );
    }

    private Mono<? extends Void> handleInvalidToken(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

}
