package jlrs.carsharing.security.auth;

import jlrs.carsharing.dto.user.auth.UserLoginRequestDto;
import jlrs.carsharing.dto.user.auth.UserLoginResponseDto;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequestDto;
import jlrs.carsharing.dto.user.UserResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto loginRequest);

    UserResponseDto register(UserRegistrationRequestDto registrationRequest);
}
