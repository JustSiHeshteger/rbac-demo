package org.zvrg.rbacdemo.service.dataservice;

import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface RbacCacheService {

    Flux<GrantedAuthority> findAuthoritiesByUserId(UUID userId);

    Mono<Void> addRole(UUID userId, String role);

    Mono<Void> saveRoles(UUID userId, List<String> roles);

    Mono<Void> deleteRole(UUID userId, String role);

}
