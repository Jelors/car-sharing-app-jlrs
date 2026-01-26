package jlrs.carsharing.dto.user.auth;

import jakarta.validation.constraints.NotBlank;
import jlrs.carsharing.validation.FieldMatch;
import jlrs.carsharing.validation.user.Email;
import jlrs.carsharing.validation.user.Name;
import jlrs.carsharing.validation.user.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch.List({
        @FieldMatch(
                field = "password",
                fieldMatch = "repeatPassword",
                message = "Password do not match!"
        )
})
public class UserRegistrationRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    @Password
    private String repeatPassword;

    @NotBlank
    @Name
    private String firstName;

    @NotBlank
    @Name
    private String lastName;

}
