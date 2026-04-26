package org.zvrg.rbacdemo.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.zvrg.rbacdemo.entity.RolePermissionEntity;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RolePermissionRepository extends ReactiveCrudRepository<RolePermissionEntity, UUID> {

    @Query("""
        SELECT r."name" AS authority
        FROM rbac_demo.roles r
        JOIN rbac_demo.user_roles ur ON r.id = ur.role_id
        WHERE ur.user_id = :userId
    
        UNION
    
        SELECT p."name" AS authority
        FROM rbac_demo.permissions p
        JOIN rbac_demo.role_permissions rp ON p.id = rp.permission_id
        JOIN rbac_demo.user_roles ur ON rp.role_id = ur.role_id
        WHERE ur.user_id = :userId
    """)
    Flux<String> findAuthoritiesByUserId(UUID userId);
}
