package org.zvrg.rbacdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.zvrg.rbacdemo.entity.UserRoleEntity;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRoleEntity, UUID> {

    Flux<UserRoleEntity> findAllByUserId(UUID userId);

}
