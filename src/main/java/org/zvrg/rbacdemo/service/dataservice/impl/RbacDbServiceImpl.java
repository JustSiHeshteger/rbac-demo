package org.zvrg.rbacdemo.service.dataservice.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.zvrg.rbacdemo.entity.PermissionEntity;
import org.zvrg.rbacdemo.entity.RoleEntity;
import org.zvrg.rbacdemo.entity.RolePermissionEntity;
import org.zvrg.rbacdemo.entity.UserEntity;
import org.zvrg.rbacdemo.entity.UserRoleEntity;
import org.zvrg.rbacdemo.repository.PermissionRepository;
import org.zvrg.rbacdemo.repository.RolePermissionRepository;
import org.zvrg.rbacdemo.repository.RoleRepository;
import org.zvrg.rbacdemo.repository.UserRepository;
import org.zvrg.rbacdemo.repository.UserRoleRepository;
import org.zvrg.rbacdemo.service.dataservice.RbacDbService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RbacDbServiceImpl implements RbacDbService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    //TODO оптимизировать
    //SELECT r.name, p.name
    //FROM user_roles ur
    //JOIN roles r ON ur.role_id = r.id
    //LEFT JOIN role_permissions rp ON r.id = rp.role_id
    //LEFT JOIN permissions p ON rp.permission_id = p.id
    //WHERE ur.user_id = :userId
    @Override
    public Flux<GrantedAuthority> getAuthoritiesByUserId(UUID userId) {
        return Mono.just(userId)
                .flatMapMany(userRoleRepository::findAllByUserId)
                .map(UserRoleEntity::getRoleId)
                .flatMap(roleRepository::findById)
                .flatMap(this::mapRoleToAuthorities)
                .distinct();
    }

    @Override
    public Flux<GrantedAuthority> getAuthoritiesByUserLogin(String login) {
        return userRepository.findByLogin(login)
                .map(UserEntity::getId)
                .flatMapMany(userRoleRepository::findAllByUserId)
                .map(UserRoleEntity::getRoleId)
                .flatMap(roleRepository::findById)
                .flatMap(this::mapRoleToAuthorities)
                .distinct();
    }

    private Flux<GrantedAuthority> mapRoleToAuthorities(RoleEntity role) {
        final Flux<GrantedAuthority> roleAuth = Flux.just(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        final Flux<GrantedAuthority> permissions = getPermissionsByRoleId(role.getId())
                .map(perm -> new SimpleGrantedAuthority(perm.getName()));

        return Flux.concat(roleAuth, permissions);
    }

    private Flux<PermissionEntity> getPermissionsByRoleId(UUID roleId) {
        return rolePermissionRepository.findAllByRoleId(roleId)
                .map(RolePermissionEntity::getPermissionId)
                .flatMap(permissionRepository::findById);
    }

}
