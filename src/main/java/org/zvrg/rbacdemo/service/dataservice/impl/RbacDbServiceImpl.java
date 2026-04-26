package org.zvrg.rbacdemo.service.dataservice.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.zvrg.rbacdemo.entity.UserEntity;
import org.zvrg.rbacdemo.repository.RolePermissionRepository;
import org.zvrg.rbacdemo.repository.UserRepository;
import org.zvrg.rbacdemo.service.dataservice.RbacDbService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RbacDbServiceImpl implements RbacDbService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public Flux<GrantedAuthority> getAuthoritiesByUserId(UUID userId) {
        return Mono.just(userId)
                .flatMapMany(rolePermissionRepository::findAuthoritiesByUserId)
                .map(SimpleGrantedAuthority::new);
    }

    @Override
    public Flux<GrantedAuthority> getAuthoritiesByUserLogin(String login) {
        return userRepository.findByLogin(login)
                .map(UserEntity::getId)
                .flatMapMany(rolePermissionRepository::findAuthoritiesByUserId)
                .map(SimpleGrantedAuthority::new);
    }

}
