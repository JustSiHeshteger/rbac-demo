package org.zvrg.rbacdemo.sevice;

import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RbacService {

    Flux<GrantedAuthority> getAuthoritiesByUserId(UUID userId);

    Flux<GrantedAuthority> getAuthoritiesByUserLogin(String login);

}
