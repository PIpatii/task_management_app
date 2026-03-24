package management.application.service;

import java.util.Optional;
import java.util.Set;
import management.application.dto.user.UpdateProfileRequestDto;
import management.application.dto.user.UpdateRoleRequestDto;
import management.application.dto.user.UserDto;
import management.application.dto.user.UserRegistrationRequestDto;
import management.application.dto.user.UserRegistrationResponseDto;
import management.application.mapper.UserMapper;
import management.application.model.Role;
import management.application.model.User;
import management.application.repository.RoleRepository;
import management.application.repository.UserRepository;
import management.application.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static management.application.helper.TestSecurityUtils.mockAuth;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @Test
    @DisplayName("user registration")
    public void register_success() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setPassword("password");
        requestDto.setRepeatPassword("password");
        requestDto.setEmail("email");

        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.USER);

        User user = new User();
        user.setId(1L);
        user.setEmail(requestDto.getEmail());
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(role));

        UserRegistrationResponseDto expected = new UserRegistrationResponseDto();
        expected.setId(user.getId());
        expected.setEmail(user.getEmail());

        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findRoleByName(Role.RoleName.USER)).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toRegistrationDto(user)).thenReturn(expected);

        UserRegistrationResponseDto actual = userService.register(requestDto);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    @DisplayName("get user's profile info")
    public void getProfileInfo_success() {
        mockAuth("email");

        User user = new User();
        user.setId(1L);
        user.setEmail("email");

        UserDto expected = new UserDto();
        expected.setEmail("email");


        when(userRepository.getUserByEmail("email")).thenReturn(user);
        when(userRepository.getUserById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserDto actual = userService.getProfileInfo();

        assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    @DisplayName("update user's profile info")
    public void updateProfileInfo_success() {
        mockAuth("email");

        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto();
        requestDto.setEmail("email");
        requestDto.setFirstName("updated firstName");
        requestDto.setLastName("updated lastName");

        User user = new User();
        user.setId(1L);
        user.setEmail("email");
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());

        UserDto expected = new UserDto();
        expected.setEmail(user.getEmail());
        expected.setFirstName(user.getFirstName());
        expected.setLastName(user.getLastName());

        when(userRepository.getUserByEmail("email")).thenReturn(user);
        when(userRepository.getUserById(1L)).thenReturn(user);
        doAnswer(invocation -> {
            UpdateProfileRequestDto dto = invocation.getArgument(0);
            User user1 = invocation.getArgument(1);

            user1.setEmail(dto.getEmail());
            user1.setFirstName(dto.getFirstName());
            user1.setLastName(dto.getLastName());
            return null;
        }).when(userMapper).updateUserFromDto(any(UpdateProfileRequestDto.class), any(User.class));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserDto actual = userService.updateProfileInfo(requestDto);

        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
    }

    @Test
    @DisplayName("update user's role")
    public void updateRole_success() {
        UpdateRoleRequestDto requestDto = new UpdateRoleRequestDto();
        requestDto.setRoleIds(Set.of(1L));

        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.USER);

        User user = new User();
        user.setId(1L);
        user.setEmail("email");
        user.setRoles(Set.of(role));

        UserDto expected = new UserDto();
        expected.setEmail(user.getEmail());
        expected.setRoleIds(Set.of(role.getId()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findRoleById(1L)).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserDto actual = userService.updateRole(requestDto, 1L);
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getRoleIds(), actual.getRoleIds());
    }

    @Test
    @DisplayName("delete a user by id")
    public void deleteUser_success() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);
        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
