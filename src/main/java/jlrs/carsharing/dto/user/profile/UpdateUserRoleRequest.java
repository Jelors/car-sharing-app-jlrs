package jlrs.carsharing.dto.user.profile;

import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.validation.user.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRoleRequest {
    @Status(enumClass = UserRole.RoleName.class)
    private UserRole role;
}
