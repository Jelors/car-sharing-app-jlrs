package jlrs.carsharing.service;

import jlrs.carsharing.dto.UpdatePasswordRequestDto;
import jlrs.carsharing.dto.UpdateProfileRequestDto;
import jlrs.carsharing.dto.UpdateUserRoleRequestDto;
import jlrs.carsharing.dto.UserResponseDto;

public interface UserService {
    UserResponseDto getProfileInfo();

    UserResponseDto updateUserRole(Long userId, UpdateUserRoleRequestDto updateRequest);

    UserResponseDto updateUserProfile(UpdateProfileRequestDto updateProfileRequest);

    UserResponseDto updateUserPassword(UpdatePasswordRequestDto updatePasswordRequest);
}
