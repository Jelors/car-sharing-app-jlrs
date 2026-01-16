package jlrs.carsharing.service.impl;

import java.util.Set;
import jlrs.carsharing.dto.UserRegistrationRequestDto;
import jlrs.carsharing.dto.UserResponseDto;
import jlrs.carsharing.exception.RegistrationException;
import jlrs.carsharing.exception.RoleNotFoundException;
import jlrs.carsharing.mapper.UserMapper;
import jlrs.carsharing.model.User;
import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.repository.UserRepository;
import jlrs.carsharing.repository.UserRoleRepository;
import jlrs.carsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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
