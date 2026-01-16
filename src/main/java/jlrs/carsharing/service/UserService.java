package jlrs.carsharing.service;

import jlrs.carsharing.dto.UserRegistrationRequestDto;
import jlrs.carsharing.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto registrationRequest);
}
