package jlrs.carsharing.dto;

import jakarta.validation.constraints.NotBlank;
import jlrs.carsharing.validation.user.Name;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDto {
    @Name
    @NotBlank
    private String firstName;

    @Name
    @NotBlank
    private String lastName;
}
