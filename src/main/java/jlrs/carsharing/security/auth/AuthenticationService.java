package jlrs.carsharing.security.auth;

import jlrs.carsharing.dto.UserLoginRequestDto;
import jlrs.carsharing.dto.UserLoginResponseDto;
import jlrs.carsharing.dto.UserRegistrationRequestDto;
import jlrs.carsharing.dto.UserResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto loginRequest);

    UserResponseDto register(UserRegistrationRequestDto registrationRequest);
}
