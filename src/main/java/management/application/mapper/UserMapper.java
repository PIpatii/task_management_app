package management.application.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import management.application.config.MapperConfig;
import management.application.dto.user.UpdateProfileRequestDto;
import management.application.dto.user.UserDto;
import management.application.dto.user.UserRegistrationRequestDto;
import management.application.dto.user.UserRegistrationResponseDto;
import management.application.model.Role;
import management.application.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toRegistrationDto(User user);

    @Mapping(target = "roleIds", ignore = true)
    UserDto toDto(User user);

    void updateUserFromDto(UpdateProfileRequestDto requestDto, @MappingTarget User user);

    @AfterMapping
    default void setRoleIds(@MappingTarget UserDto userDto, User user) {
        Set<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        userDto.setRoleIds(roleIds);
    }
}
