package jlrs.carsharing.mapper;

import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.user.UserResponse;
import jlrs.carsharing.dto.user.auth.UserRegistrationRequest;
import jlrs.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequest createUserRequestDto);

    UserResponse toUserResponse(User user);

    void updateUserFromDto(UserRegistrationRequest changedUser,
                           @MappingTarget User user);
}
