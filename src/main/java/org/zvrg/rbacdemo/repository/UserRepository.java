package org.zvrg.rbacdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.zvrg.rbacdemo.entity.UserEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    Mono<UserEntity> findByLogin(String login);

    Mono<Boolean> existsByLogin(String login);

}
