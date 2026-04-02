package management.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import management.application.dto.user.UpdateProfileRequestDto;
import management.application.dto.user.UpdateRoleRequestDto;
import management.application.dto.user.UserDto;
import management.application.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "user", description = "users controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "get profile info", description = "get user's profile info")
    public UserDto getProfileInfo() {
        return userService.getProfileInfo();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping
    @Operation(summary = "update profile info", description = "update user's profile info")
    public UserDto updateProfileInfo(@RequestBody @Valid UpdateProfileRequestDto requestDto) {
        return userService.updateProfileInfo(requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    @Operation(summary = "update role", description = "update a role by id")
    public UserDto updateRole(@RequestBody @Valid UpdateRoleRequestDto requestDto,
                              @PathVariable Long id) {
        return userService.updateRole(requestDto, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "delete user", description = "delete a user by id")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
