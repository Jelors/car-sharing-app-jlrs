package jlrs.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.auth.UserLoginRequest;
import jlrs.carsharing.dto.user.auth.UserLoginResponse;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequest;
import jlrs.carsharing.exception.RegistrationException;
import jlrs.carsharing.mapper.UserMapper;
import jlrs.carsharing.model.User;
import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.repository.UserRepository;
import jlrs.carsharing.repository.UserRoleRepository;
import jlrs.carsharing.security.JwtUtil;
import jlrs.carsharing.security.auth.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private UserRegistrationRequest registrationRequest;
    private User user;
    private UserRole customerRole;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("raw_password");

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("raw_password");

        customerRole = new UserRole();
        customerRole.setRole(UserRole.RoleName.CUSTOMER);
    }

    @Test
    @DisplayName("""
            Register - Success
            """)
    void register_ValidRequest_Success() throws RegistrationException {
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userRoleRepository.findByRole(UserRole.RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(userMapper.toModel(registrationRequest)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userMapper.toUserResponse(any())).thenReturn(new UserResponse());

        UserResponse response = authenticationService.register(registrationRequest);

        assertNotNull(response);
        verify(userRepository).save(user);
        assertEquals("hashed_password", user.getPassword());
        assertTrue(user.getRoles().contains(customerRole));
    }

    @Test
    @DisplayName("""
            Register - Email already exists
            """)
    void register_EmailExists_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RegistrationException.class, () ->
                authenticationService.register(registrationRequest)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("""
            Authenticate - Success
            """)
    void authenticate_ValidRequest_ReturnsToken() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(jwtUtil.generateToken("test@example.com")).thenReturn("mocked_jwt_token");

        UserLoginResponse response = authenticationService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
        verify(jwtUtil).generateToken("test@example.com");
    }
}