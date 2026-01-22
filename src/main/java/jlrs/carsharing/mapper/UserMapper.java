package jlrs.carsharing.mapper;

import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.user.UserResponseDto;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequestDto;
import jlrs.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequestDto createUserRequestDto);

    UserResponseDto toUserResponse(User user);

    void updateUserFromDto(UserRegistrationRequestDto changedUser,
                           @MappingTarget User user);
}
