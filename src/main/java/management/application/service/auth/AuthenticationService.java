package management.application.service.auth;

import management.application.dto.user.UserLoginRequestDto;
import management.application.dto.user.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto login(UserLoginRequestDto request);
}
