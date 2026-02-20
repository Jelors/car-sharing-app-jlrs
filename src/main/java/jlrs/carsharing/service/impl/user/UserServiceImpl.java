package jlrs.carsharing.service.impl.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.profile.UpdatePasswordRequest;
import jlrs.carsharing.dto.user.profile.UpdateProfileRequest;
import jlrs.carsharing.dto.user.profile.UpdateUserRoleRequest;
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
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    /*
        return's info about user: {EMAIL}, {FIRST_NAME}, {LAST_NAME}
    */
    @Override
    public UserResponse getProfileInfo() {
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
    public UserResponse updateUserRole(
            Long id,
            UpdateUserRoleRequest updateRequest
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id {" + id + "} not found!"
                ));

        UserRole userRole = userRoleRepository.findByRole(updateRequest.getRole())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + updateRequest.getRole()));

        user.setRoles(new HashSet<>(Set.of(userRole)));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    /*
        update user profile. !!! EMAIL CAN'T BE UPDATED !!!
        request needs: {FIRST_NAME}, {LAST_NAME}
        returns: {EMAIL}, {FIRST_NAME}, {LAST_NAME}
     */
    @Override
    public UserResponse updateUserProfile(UpdateProfileRequest updateProfileRequest) {
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
    public UserResponse updateUserPassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = userDetailsService.getCurrentUser();
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
