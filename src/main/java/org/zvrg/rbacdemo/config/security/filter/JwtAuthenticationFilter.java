package org.zvrg.rbacdemo.config.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.zvrg.rbacdemo.service.dataservice.RbacService;
import org.zvrg.rbacdemo.service.securityservice.JwtService;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final RbacService rbacService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return jwtService.extractToken(exchange)
                .switchIfEmpty(chain.filter(exchange)
                        .then(Mono.empty())
                )
                .flatMap(token -> jwtService.validateToken(token)
                        .flatMap(isValid -> isValid
                                ? authenticateAndContinue(token, exchange, chain)
                                : handleInvalidToken(exchange)
                        )
                )
                .onErrorResume(ex -> {
                    log.error(ex.getMessage(), ex);
                    return handleInvalidToken(exchange);
                });
    }

    private Mono<Void> authenticateAndContinue(String token,
                                                         ServerWebExchange exchange,
                                                         WebFilterChain chain) {
        // TODO сделать refresh токен
        return jwtService.extractSubject(token)
                .map(UUID::fromString)
                .flatMap(userId -> rbacService.findRolesByUserId(userId)
                                .collectList()
                                .map(roles -> new UsernamePasswordAuthenticationToken(userId,null, roles))
                )
                .flatMap(auth -> chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                );
    }

    private Mono<Void> handleInvalidToken(ServerWebExchange exchange) {
        return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                        .then(exchange.getResponse().setComplete());
    }

}
