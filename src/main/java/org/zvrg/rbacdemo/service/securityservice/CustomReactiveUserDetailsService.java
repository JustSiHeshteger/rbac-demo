package org.zvrg.rbacdemo.service.securityservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.zvrg.rbacdemo.dto.SecurityUser;
import org.zvrg.rbacdemo.repository.UserRepository;
import org.zvrg.rbacdemo.service.dataservice.RbacService;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final RbacService rbacService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByLogin(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Test")))
                .flatMap(userEntity ->
                     rbacService.findRolesByUserId(userEntity.getId())
                            .collectList()
                            .map(authorities -> SecurityUser
                                    .builder()
                                    .id(userEntity.getId())
                                    .username(userEntity.getLogin())
                                    .password(userEntity.getPassword())
                                    .authorities(authorities)
                                    .build()
                            )
                );
    }

}
