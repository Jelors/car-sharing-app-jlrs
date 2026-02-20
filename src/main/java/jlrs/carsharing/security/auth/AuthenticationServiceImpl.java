package jlrs.carsharing.security.auth;

import java.util.Set;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.auth.UserLoginRequest;
import jlrs.carsharing.dto.user.auth.UserLoginResponse;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequest;
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

    /*
        authenticate user.
        request needs: {EMAIL}, {PASSWORD}
        returns: {TOKEN}
     */
    @Override
    public UserLoginResponse authenticate(UserLoginRequest loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponse(token);
    }

    /*
        register a new user.
        request needs: {EMAIL}, {PASSWORD}, {REPEAT_PASSWORD}, {FIRST_NAME}, {LAST_NAME}
        returns: {EMAIL}, {FIRST_NAME}, {LAST_NAME}
    */
    @Override
    public UserResponse register(UserRegistrationRequest registrationRequest)
            throws RegistrationException {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new RegistrationException("User with email "
                    + registrationRequest.getEmail()
                    + " already registered!");
        }

        UserRole userRole = userRoleRepository
                .findByRole(UserRole.RoleName.CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException("Role CUSTOMER not found!"));

        User user = userMapper.toModel(registrationRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }
}
