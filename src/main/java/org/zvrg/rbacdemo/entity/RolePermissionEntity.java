package org.zvrg.rbacdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

import static org.zvrg.rbacdemo.common.Constants.EntityConstants.SCHEMA_NAME;

@Table(schema = SCHEMA_NAME, name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionEntity {

    private UUID roleId;

    private UUID permissionId;

}
