package org.zvrg.rbacdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.zvrg.rbacdemo.entity.PermissionEntity;

import java.util.UUID;

public interface PermissionRepository extends ReactiveCrudRepository<PermissionEntity, UUID> {

}
