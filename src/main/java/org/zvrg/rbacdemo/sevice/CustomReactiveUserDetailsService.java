package org.zvrg.rbacdemo.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.zvrg.rbacdemo.repository.UserRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final RbacService rbacService;
    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByLogin(username)
                .flatMap(userEntity ->
                        rbacService.getAuthoritiesByUserId(userEntity.getId())
                                .collectList()
                                .map(authorities ->
                                        User.withUsername(userEntity.getLogin())
                                            .password(userEntity.getPassword())
                                            .authorities(authorities)
                                            .build()
                                )
                );
    }

}
