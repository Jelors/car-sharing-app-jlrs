package jlrs.carsharing.security.auth;

import java.util.Set;
import jlrs.carsharing.dto.UserLoginRequestDto;
import jlrs.carsharing.dto.UserLoginResponseDto;
import jlrs.carsharing.dto.UserRegistrationRequestDto;
import jlrs.carsharing.dto.UserResponseDto;
import jlrs.carsharing.exception.RegistrationException;
import jlrs.carsharing.exception.RoleNotFoundException;
import jlrs.carsharing.mapper.UserMapper;
import jlrs.carsharing.model.User;
import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.repository.UserRepository;
import jlrs.carsharing.repository.UserRoleRepository;
import jlrs.carsharing.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserLoginResponseDto authenticate(UserLoginRequestDto loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }

    @Override
    public UserResponseDto register(UserRegistrationRequestDto registrationRequest)
            throws RegistrationException {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new RegistrationException("User with email "
                    + registrationRequest.getEmail()
                    + " already registered!");
        }

        UserRole userRole = userRoleRepository
                .findByRoleName(UserRole.RoleName.CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException("Role CUSTOMER not found!"));

        User user = userMapper.toModel(registrationRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }
}
