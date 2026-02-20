package jlrs.carsharing.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.profile.UpdatePasswordRequest;
import jlrs.carsharing.dto.user.profile.UpdateProfileRequest;
import jlrs.carsharing.dto.user.profile.UpdateUserRoleRequest;
import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.security.JwtAuthenticationFilter;
import jlrs.carsharing.security.JwtUtil;
import jlrs.carsharing.service.UserService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setEmail("test@example.com");
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
    }

    @Test
    @DisplayName("GET /users/me - Should return profile info")
    void receiveProfileInfo_ReturnsOk() throws Exception {
        when(userService.getProfileInfo()).thenReturn(userResponse);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @DisplayName("PUT /users/{id}/role - Should update role and return 200")
    void updateUserRole_ValidRequest_ReturnsOk() throws Exception {
        UpdateUserRoleRequest roleRequest = new UpdateUserRoleRequest();

        roleRequest.setRole(UserRole.RoleName.MANAGER);

        when(userService.updateUserRole(anyLong(), any(UpdateUserRoleRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("PUT /users/me/info/updateProfile - Should update profile")
    void updateProfileInfo_ValidRequest_ReturnsOk() throws Exception {
        UpdateProfileRequest profileRequest = new UpdateProfileRequest();
        profileRequest.setFirstName("NewFirstName");
        profileRequest.setLastName("NewLastName");

        userResponse.setFirstName("NewFirstName");
        userResponse.setLastName("NewLastName");
        when(userService.updateUserProfile(any(UpdateProfileRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/users/me/info/updateProfile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewFirstName"));
    }

    @Test
    @DisplayName("PUT /users/me/info/updatePassword - Should return 204 No Content")
    void updatePassword_ValidRequest_ReturnsNoContent() throws Exception {
        UpdatePasswordRequest passwordRequest = new UpdatePasswordRequest();
        passwordRequest.setPassword("new_pass");
        passwordRequest.setRepeatPassword("new_pass");

        mockMvc.perform(put("/users/me/info/updatePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isNoContent());
    }
}