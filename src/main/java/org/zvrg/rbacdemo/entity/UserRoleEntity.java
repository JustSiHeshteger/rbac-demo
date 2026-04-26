package org.zvrg.rbacdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

import static org.zvrg.rbacdemo.common.Constants.EntityConstants.SCHEMA_NAME;

@Table(schema = SCHEMA_NAME, name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class UserRoleEntity {

    private UUID userId;

    private UUID roleId;

}
