package org.zvrg.rbacdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.zvrg.rbacdemo.entity.RoleEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository extends ReactiveCrudRepository<RoleEntity, UUID> {

    Mono<RoleEntity> findByName(String name);

}
