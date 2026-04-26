package org.zvrg.rbacdemo.service.dataservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RbacService {

    private final RbacDbService rbacDbService;
    private final RbacCacheService rbacCacheService;

    public Flux<GrantedAuthority> findRolesByUserId(UUID userId) {
        return rbacCacheService.findAuthoritiesByUserId(userId)
                .switchIfEmpty(Flux.defer(() -> rbacDbService.getAuthoritiesByUserId(userId)
                        .collectList()
                        .flatMapMany(roles -> {
                            if (roles.isEmpty()) return Flux.empty();

                            final var rolesToSave = roles.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList();

                            return rbacCacheService.saveRoles(userId, rolesToSave)
                                    .thenMany(Flux.fromIterable(roles));
                        }))
                );
    }

}
