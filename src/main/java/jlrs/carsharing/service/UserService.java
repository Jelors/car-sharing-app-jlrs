package jlrs.carsharing.service;

import jlrs.carsharing.dto.user.profile.UpdatePasswordRequestDto;
import jlrs.carsharing.dto.user.profile.UpdateProfileRequestDto;
import jlrs.carsharing.dto.user.profile.UpdateUserRoleRequestDto;
import jlrs.carsharing.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto getProfileInfo();

    UserResponseDto updateUserRole(Long userId, UpdateUserRoleRequestDto updateRequest);

    UserResponseDto updateUserProfile(UpdateProfileRequestDto updateProfileRequest);

    UserResponseDto updateUserPassword(UpdatePasswordRequestDto updatePasswordRequest);
}
