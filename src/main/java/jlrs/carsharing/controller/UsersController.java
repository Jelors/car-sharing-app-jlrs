package jlrs.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.profile.UpdatePasswordRequest;
import jlrs.carsharing.dto.user.profile.UpdateProfileRequest;
import jlrs.carsharing.dto.user.profile.UpdateUserRoleRequest;
import jlrs.carsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users Manager", description = "Endpoints for managing users")
public class UsersController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Get profile info",
            description = "Receive profile info about current logged user"
    )
    public ResponseEntity<UserResponse> receiveProfileInfo() {
        return new ResponseEntity<>(
                userService.getProfileInfo(),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Update user role",
            description = "Updates user role"
    )
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleRequest roleRequestDto
    ) {
        return new ResponseEntity<>(
                userService.updateUserRole(id, roleRequestDto),
                HttpStatus.OK
        );
    }

    @PutMapping("/me/info/updateProfile")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Update profile",
            description = "Updates profile current logged user"
    )
    public ResponseEntity<UserResponse> updateProfileInfo(
            @RequestBody @Valid UpdateProfileRequest profileRequestDto
    ) {
        return new ResponseEntity<>(
                userService.updateUserProfile(profileRequestDto),
                HttpStatus.OK
        );
    }

    @PutMapping("/me/info/updatePassword")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Update password",
            description = "Updates password current logged user"
    )
    public ResponseEntity<UserResponse> updatePassword(
            @RequestBody @Valid UpdatePasswordRequest passwordRequestDto
    ) {
        return new ResponseEntity<>(
                userService.updateUserPassword(passwordRequestDto),
                HttpStatus.NO_CONTENT
        );
    }

}
