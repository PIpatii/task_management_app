package management.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import management.application.dto.user.UserLoginRequestDto;
import management.application.dto.user.UserLoginResponseDto;
import management.application.dto.user.UserRegistrationRequestDto;
import management.application.dto.user.UserRegistrationResponseDto;
import management.application.service.UserService;
import management.application.service.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class AuthControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    public void beforeEach(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("user registration")
    public void register_success() throws Exception {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("new@gmail.com");
        request.setPassword("123456");
        request.setRepeatPassword("123456");
        request.setFirstName("firstName");
        request.setLastName("lastName");

        UserRegistrationResponseDto response = new UserRegistrationResponseDto();
        response.setId(10L);
        response.setEmail("new@gmail.com");

        when(userService.register(any(UserRegistrationRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@gmail.com"));

        verify(userService).register(any(UserRegistrationRequestDto.class));
    }

    @Test
    @DisplayName("user log in")
    public void login_success() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail("email");
        request.setPassword("password");

        UserLoginResponseDto response = new UserLoginResponseDto("jwt-token");

        when(authenticationService.login(any(UserLoginRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authenticationService).login(any(UserLoginRequestDto.class));
    }
}
