package jlrs.carsharing.security.auth;

import jlrs.carsharing.dto.UserLoginRequestDto;
import jlrs.carsharing.dto.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto loginRequest);
}
