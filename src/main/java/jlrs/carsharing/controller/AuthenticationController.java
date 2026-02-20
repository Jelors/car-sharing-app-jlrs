package jlrs.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.auth.UserLoginRequest;
import jlrs.carsharing.dto.user.auth.UserLoginResponse;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequest;
import jlrs.carsharing.security.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication Manager", description = "Endpoints for managing authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(
            summary = "Register a new user",
            description = "Register a new user"
    )
    public ResponseEntity<UserResponse> register(
            @RequestBody UserRegistrationRequest registrationRequest
    ) {
        return new ResponseEntity<UserResponse>(
                authenticationService.register(registrationRequest),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate user by email and password"
    )
    public ResponseEntity<UserLoginResponse> login(
            @RequestBody UserLoginRequest loginRequest
    ) {
        return new ResponseEntity<UserLoginResponse>(
                authenticationService.authenticate(loginRequest),
                HttpStatus.OK
        );
    }

}
