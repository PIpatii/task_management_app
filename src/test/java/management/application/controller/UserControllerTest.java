package management.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import management.application.dto.user.UpdateProfileRequestDto;
import management.application.dto.user.UpdateRoleRequestDto;
import management.application.dto.user.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static management.application.helper.TestDataHelper.createUpdateProfileRequestDto;
import static management.application.helper.TestDataHelper.createUpdateRoleRequestDto;
import static management.application.helper.TestDataHelper.createUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class UserControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource,
                           @Autowired WebApplicationContext webApplicationContext) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/user/add-element-to-user-table.sql"));
        }
    }

    @AfterEach
    public void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/user/remove-all-elements-from-user-table.sql"));
        }
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get user's profile info")
    public void getProfileInfo_success() throws Exception {
        UserDto expected = createUserDto("email","firstName", "lastName");;

        MvcResult mvcResult = mockMvc
                                .perform(get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("update user's profile info")
    public void updateProfileInfo_success() throws Exception {
        UpdateProfileRequestDto requestDto = createUpdateProfileRequestDto("email",
                "lastName", "firstName");

        MvcResult mvcResult = mockMvc
                .perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertEquals(requestDto.getFirstName(), actual.getFirstName());
        assertEquals(requestDto.getLastName(), actual.getLastName());
        assertEquals(requestDto.getEmail(), actual.getEmail());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("update user's role")
    public void updateRole_success() throws Exception {
        UpdateRoleRequestDto requestDto = createUpdateRoleRequestDto(Set.of(2L));

        MvcResult mvcResult = mockMvc
                .perform(put("/users/{id}/roles", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertEquals(requestDto.getRoleIds(), actual.getRoleIds());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("delete a user by id")
    public void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/users/{id}", 2L))
                .andExpect(status().isNoContent());
    }
}
