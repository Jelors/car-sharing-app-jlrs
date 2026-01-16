package jlrs.carsharing.service.impl;

import jlrs.carsharing.dto.UserRegistrationRequestDto;
import jlrs.carsharing.dto.UserResponseDto;
import jlrs.carsharing.mapper.UserMapper;
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
    public UserResponseDto register(UserRegistrationRequestDto registrationRequest) {
        return null;
    }
}
