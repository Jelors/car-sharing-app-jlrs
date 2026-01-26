package jlrs.carsharing.service;

import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.profile.UpdatePasswordRequest;
import jlrs.carsharing.dto.user.profile.UpdateProfileRequest;
import jlrs.carsharing.dto.user.profile.UpdateUserRoleRequest;

public interface UserService {
    UserResponse getProfileInfo();

    UserResponse updateUserRole(Long userId, UpdateUserRoleRequest updateRequest);

    UserResponse updateUserProfile(UpdateProfileRequest updateProfileRequest);

    UserResponse updateUserPassword(UpdatePasswordRequest updatePasswordRequest);
}
