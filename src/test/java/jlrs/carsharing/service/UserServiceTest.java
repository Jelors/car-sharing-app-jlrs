package jlrs.carsharing.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.profile.UpdatePasswordRequest;
import jlrs.carsharing.dto.user.profile.UpdateProfileRequest;
import jlrs.carsharing.dto.user.profile.UpdateUserRoleRequest;
import jlrs.carsharing.mapper.UserMapper;
import jlrs.carsharing.model.User;
import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.repository.UserRepository;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import jlrs.carsharing.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("old_password");

        userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
    }

    @Test
    @DisplayName("""
            Should return current user's profile
            """)
    void getProfileInfo_ValidUser_ReturnsUserResponse() {
        String email = "test@example.com";
        when(userDetailsService.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getProfileInfo();

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("""
            If user not found will be thrown exception
            """)
    void getProfileInfo_UserNotFound_ThrowsException() {
        when(userDetailsService.getCurrentUserEmail()).thenReturn("fake@mail.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfileInfo())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Can't load user's profile!");
    }

    @Test
    @DisplayName("""
            Update user's role
            """)
    void updateUserRole_ValidRequest_ReturnsUpdatedUser() {
        Long userId = 1L;
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setRole(UserRole.RoleName.MANAGER);
        userRole.setDeleted(false);
        UpdateUserRoleRequest roleRequest = new UpdateUserRoleRequest();
        roleRequest.setRole(userRole);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUserRole(userId, roleRequest);

        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("""
            Update user's firstName and lastName
            """)
    void updateUserProfile_ValidRequest_UpdatesData() {
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setFirstName("NewName");
        updateRequest.setLastName("NewLastName");

        when(userDetailsService.getCurrentUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        userService.updateUserProfile(updateRequest);

        assertThat(user.getFirstName()).isEqualTo("NewName");
        assertThat(user.getLastName()).isEqualTo("NewLastName");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("""
            Update user's password
            """)
    void updateUserPassword_ValidRequest_EncodesPassword() {
        UpdatePasswordRequest passwordRequest = new UpdatePasswordRequest();
        passwordRequest.setPassword("new_secret_123");

        String encodedPassword = "encoded_hash_string";

        when(userDetailsService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.encode(passwordRequest.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        userService.updateUserPassword(passwordRequest);

        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        verify(passwordEncoder).encode("new_secret_123");
        verify(userRepository).save(user);
    }
}
