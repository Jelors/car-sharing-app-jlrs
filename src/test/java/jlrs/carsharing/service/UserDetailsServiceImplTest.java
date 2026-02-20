package jlrs.carsharing.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import jlrs.carsharing.model.User;
import jlrs.carsharing.repository.UserRepository;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(10L);
        testUser.setEmail("test@carsharing.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("""
            Return user email if he/she exists
            """)
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        UserDetails result = userDetailsService.loadUserByUsername(testUser.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("""
            If can't find email, will be thrown exception
            """)
    void loadUserByUsername_UserDoesNotExist_ThrowsException() {
        when(userRepository.findByEmail("wrong@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("wrong@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Can't find user with email");
    }

    @Test
    @DisplayName("""
            Returns current user's ID
            """)
    void getCurrentUserId_Authenticated_ReturnsId() {
        mockSecurityContext(testUser);

        Long id = userDetailsService.getCurrentUserId();

        assertThat(id).isEqualTo(10L);
    }

    @Test
    @DisplayName("""
            If user ain't authenticated will be thrown exception
            """)
    void getCurrentUserId_NotAuthenticated_ThrowsException() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> userDetailsService.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User is not authenticated");
    }

    @Test
    @DisplayName("""
            Get user from DB
            """)
    void getCurrentUser_ValidSession_ReturnsUser() {
        mockSecurityContext(testUser);
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));

        User result = userDetailsService.getCurrentUser();

        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findById(10L);
    }

    @Test
    @DisplayName("""
            Returns current user's email
            """)
    void getCurrentUserEmail_ValidSession_ReturnsEmail() {
        mockSecurityContext(testUser);
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));

        String email = userDetailsService.getCurrentUserEmail();

        assertThat(email).isEqualTo("test@carsharing.com");
    }

    @Test
    @DisplayName("""
            Throws exception if ID didn't found in DB
            """)
    void getCurrentUser_UserMissingInDb_ThrowsException() {
        mockSecurityContext(testUser);
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.getCurrentUser())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id 10 not found!");
    }

    private void mockSecurityContext(User principal) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(principal);
    }
}
