package jlrs.carsharing.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.auth.UserLoginRequest;
import jlrs.carsharing.dto.user.auth.UserLoginResponse;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequest;
import jlrs.carsharing.security.JwtAuthenticationFilter;
import jlrs.carsharing.security.JwtUtil;
import jlrs.carsharing.security.auth.AuthenticationService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter authenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("""
            POST /auth/registration - success
            """)
    void register_ValidRequest_ReturnsCreated() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setEmail("test@example.com");

        when(authenticationService.register(any(UserRegistrationRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("""
            POST /auth/login - success
            """)
    void login_ValidRequest_ReturnsOk() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        UserLoginResponse expectedResponse = new UserLoginResponse("mocked-jwt-token");

        when(authenticationService.authenticate(any(UserLoginRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }
}