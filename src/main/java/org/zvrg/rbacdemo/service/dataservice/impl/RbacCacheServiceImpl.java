package org.zvrg.rbacdemo.service.dataservice.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.zvrg.rbacdemo.service.dataservice.RbacCacheService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

//TODO добавить onErrorResume
@Service
@RequiredArgsConstructor
public class RbacCacheServiceImpl implements RbacCacheService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public Flux<GrantedAuthority> findAuthoritiesByUserId(UUID userId) {
        return reactiveRedisTemplate.opsForSet()
                .members(buildKey(userId))
                .map(SimpleGrantedAuthority::new);
    }

    @Override
    public Mono<Void> addRole(UUID userId, String role) {
        final var key = buildKey(userId);
        return reactiveRedisTemplate.opsForSet()
                .add(key, role)
                .then(reactiveRedisTemplate.expire(key, Duration.ofHours(1)))
                .then();
    }

    @Override
    public Mono<Void> saveRoles(UUID userId, List<String> roles) {
        final var key = buildKey(userId);
        return reactiveRedisTemplate.delete(key)
                .then(reactiveRedisTemplate.opsForSet()
                        .add(key, roles.toArray(new String[0]))
                )
                .then(reactiveRedisTemplate.expire(key, Duration.ofHours(1)))
                .then();
    }

    @Override
    public Mono<Void> deleteRole(UUID userId, String role) {
        return reactiveRedisTemplate
                .delete(buildKey(userId))
                .then();
    }

    private String buildKey(UUID userId) {
        return "user:roles:" + userId.toString();
    }

}
