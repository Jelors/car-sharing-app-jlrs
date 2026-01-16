package jlrs.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jlrs.carsharing.dto.UserLoginRequestDto;
import jlrs.carsharing.dto.UserLoginResponseDto;
import jlrs.carsharing.dto.UserRegistrationRequestDto;
import jlrs.carsharing.dto.UserResponseDto;
import jlrs.carsharing.security.auth.AuthenticationService;
import jlrs.carsharing.service.UserService;
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
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Register a new user", description = "Register a new user")
    public ResponseEntity<UserResponseDto> register(
            @RequestBody @Valid UserRegistrationRequestDto registrationRequest
    ) {
        return new ResponseEntity<UserResponseDto>(
                userService.register(registrationRequest),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticate user by email and password")
    public ResponseEntity<UserLoginResponseDto> login(
            @RequestBody @Valid UserLoginRequestDto loginRequest
    ) {
        return new ResponseEntity<UserLoginResponseDto>(
                authenticationService.authenticate(loginRequest),
                HttpStatus.OK
        );
    }
}
