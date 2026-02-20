package jlrs.carsharing.security.auth;

import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.auth.UserLoginRequest;
import jlrs.carsharing.dto.user.auth.UserLoginResponse;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequest;

public interface AuthenticationService {
    UserLoginResponse authenticate(UserLoginRequest loginRequest);

    UserResponse register(UserRegistrationRequest registrationRequest);
}
