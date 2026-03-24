package management.application.service;

import management.application.dto.user.UpdateProfileRequestDto;
import management.application.dto.user.UpdateRoleRequestDto;
import management.application.dto.user.UserDto;
import management.application.dto.user.UserRegistrationRequestDto;
import management.application.dto.user.UserRegistrationResponseDto;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto);

    UserDto getProfileInfo();

    UserDto updateProfileInfo(UpdateProfileRequestDto requestDto);

    UserDto updateRole(UpdateRoleRequestDto requestDto, Long userId);

    void deleteUser(Long userId);
}
