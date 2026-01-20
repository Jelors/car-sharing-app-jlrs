package jlrs.carsharing.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jlrs.carsharing.model.User;
import jlrs.carsharing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("TRYING TO FIND USER WITH EMAIL: " + username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    System.out.println("USER NOT FOUND IN DB");
                    return new UsernameNotFoundException(username);
                });
        System.out.println("USER FOUND: " + user.getEmail());
        return user;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        User user = (User) authentication.getPrincipal();
        return user != null ? user.getId() : null;
    }

    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id " + userId + " not found!"
                ));
    }

    public String getCurrentUserEmail() {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id {" + userId + "} not found!"
                ));
        return user.getEmail();
    }

}
