package management.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import management.application.dto.user.UpdateProfileRequestDto;
import management.application.dto.user.UpdateRoleRequestDto;
import management.application.dto.user.UserDto;
import management.application.dto.user.UserRegistrationRequestDto;
import management.application.dto.user.UserRegistrationResponseDto;
import management.application.exception.RegistrationException;
import management.application.mapper.UserMapper;
import management.application.model.Role;
import management.application.model.User;
import management.application.repository.RoleRepository;
import management.application.repository.UserRepository;
import management.application.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());

        if (user.isEmpty()) {
            User newUser = userMapper.toEntity(requestDto);
            newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            Role defaultRole = roleRepository.findRoleByName(Role.RoleName.USER);
            newUser.setRoles(Set.of(defaultRole));
            userRepository.save(newUser);

            return userMapper.toRegistrationDto(newUser);
        }
        throw new RegistrationException("User already exists");
    }

    @Override
    public UserDto getProfileInfo() {
        User user = userRepository.getUserById(getUserId());

        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateProfileInfo(UpdateProfileRequestDto requestDto) {
        User user = userRepository.getUserById(getUserId());
        userMapper.updateUserFromDto(requestDto, user);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateRole(UpdateRoleRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<Role> roles = requestDto.getRoleIds()
                .stream()
                .map(roleRepository::findRoleById)
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private Long getUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.getUserByEmail(username).getId();
    }
}
