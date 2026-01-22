package jlrs.carsharing.security.auth;

import jlrs.carsharing.dto.user.UserResponseDto;
import jlrs.carsharing.dto.user.auth.UserLoginRequestDto;
import jlrs.carsharing.dto.user.auth.UserLoginResponseDto;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequestDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto loginRequest);

    UserResponseDto register(UserRegistrationRequestDto registrationRequest);
}
