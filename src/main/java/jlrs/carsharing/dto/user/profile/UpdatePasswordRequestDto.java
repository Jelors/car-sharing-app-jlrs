package jlrs.carsharing.dto.user.profile;

import jakarta.validation.constraints.NotBlank;
import jlrs.carsharing.validation.FieldMatch;
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
public class UpdatePasswordRequestDto {
    @Password
    @NotBlank
    private String password;

    @Password
    @NotBlank
    private String repeatPassword;
}
