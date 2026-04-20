package org.zvrg.rbacdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.zvrg.rbacdemo.entity.RolePermissionEntity;
import org.zvrg.rbacdemo.entity.UserRoleEntity;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RolePermissionRepository extends ReactiveCrudRepository<RolePermissionEntity, UUID> {

    Flux<RolePermissionEntity> findAllByRoleId(UUID roleId);

}
