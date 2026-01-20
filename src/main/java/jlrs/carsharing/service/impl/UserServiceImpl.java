package jlrs.carsharing.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import jlrs.carsharing.dto.UpdatePasswordRequestDto;
import jlrs.carsharing.dto.UpdateProfileRequestDto;
import jlrs.carsharing.dto.UpdateUserRoleRequestDto;
import jlrs.carsharing.dto.UserResponseDto;
import jlrs.carsharing.mapper.UserMapper;
import jlrs.carsharing.model.User;
import jlrs.carsharing.repository.UserRepository;
import jlrs.carsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /*
        return's info about user: {EMAIL}, {FIRST_NAME}, {LAST_NAME}
    */
    @Override
    public UserResponseDto getProfileInfo() {
        String email = userDetailsService.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new EntityNotFoundException("Can't load user's profile!"));
    }

    /*
        update user role.
        request needs: {USER_ID}, {ROLE}
        returns: {EMAIL}, {FIRST_NAME}, {LAST_NAME}
     */
    @Override
    public UserResponseDto updateUserRole(
            Long userId,
            UpdateUserRoleRequestDto updateRequest
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id {" + userId + "} not found!"
                ));
        user.setRoles(Set.of(updateRequest.getRole()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    /*
        update user profile. !!! EMAIL CAN'T BE UPDATED !!!
        request needs: {FIRST_NAME}, {LAST_NAME}
        returns: {EMAIL}, {FIRST_NAME}, {LAST_NAME}
     */
    @Override
    public UserResponseDto updateUserProfile(UpdateProfileRequestDto updateProfileRequest) {
        User user = userDetailsService.getCurrentUser();
        user.setFirstName(updateProfileRequest.getFirstName());
        user.setLastName(updateProfileRequest.getLastName());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    /*
        update user password.
        request needs: {PASSWORD}, {REPEAT_PASSWORD}
        returns: {EMAIL}, {FIRST_NAME}, {LAST_NAME}
     */
    @Override
    public UserResponseDto updateUserPassword(UpdatePasswordRequestDto updatePasswordRequest) {
        User user = userDetailsService.getCurrentUser();
        user.setPassword(updatePasswordRequest.getPassword());
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
