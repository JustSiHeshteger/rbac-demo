package org.zvrg.rbacdemo.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.zvrg.rbacdemo.entity.UserRoleEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRoleEntity, UUID> {

    @Modifying
    @Query("INSERT INTO rbac_demo.user_roles (user_id, role_id) VALUES (:userId, :roleId)")
    Mono<Void> insertRole(UUID userId, UUID roleId);

}
