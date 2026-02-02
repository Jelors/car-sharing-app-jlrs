package jlrs.carsharing.dto.user.auth;

import jakarta.validation.constraints.NotBlank;
import jlrs.carsharing.validation.user.Email;
import jlrs.carsharing.validation.user.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Password
    private String password;
}
