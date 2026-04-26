package org.zvrg.rbacdemo.service.dataservice;

import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RbacDbService {

    Flux<GrantedAuthority> getAuthoritiesByUserId(UUID userId);

    Flux<GrantedAuthority> getAuthoritiesByUserLogin(String login);

}
