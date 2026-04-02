package management.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import management.application.dto.project.CreateProjectRequestDto;
import management.application.dto.project.ProjectDto;
import management.application.dto.project.UpdateProjectRequestDto;
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
import static management.application.helper.TestDataHelper.createCreateProjectRequestDto;
import static management.application.helper.TestDataHelper.createProjectDto;
import static management.application.helper.TestDataHelper.createUpdateProjectRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class ProjectControllerTest {
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
                    new ClassPathResource("database/project/add-two-elements-to-projects-table.sql"));
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
                    new ClassPathResource("database/project/remove-all-elements-from-projects-table.sql"));
        }
    }


    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get all user's projects")
    public void getProjects_success() throws Exception {
        int expectedSize = 2;

        MvcResult mvcResult = mockMvc.perform(
                        get("/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        ProjectDto[] actual = objectMapper.readValue(
                root.get("content").toString(), ProjectDto[].class);

        assertEquals(expectedSize, actual.length);
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get a project by id")
    public void getProjectById_success() throws Exception {
        ProjectDto expected = createProjectDto(1L, "project", "description",
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "IN_PROGRESS", 2L);

        MvcResult mvcResult = mockMvc
                        .perform(get("/projects/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ProjectDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getAssigneeId(), actual.getAssigneeId());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("create a project")
    public void createProject_success() throws Exception{
        CreateProjectRequestDto requestDto = createCreateProjectRequestDto("orange",
                "description", LocalDate.now(), LocalDate.now().plusMonths(1), 1L);

        MvcResult mvcResult = mockMvc
                .perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        ProjectDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);

        assertEquals(requestDto.getAssigneeId(), actual.getAssigneeId());
        assertEquals(requestDto.getName(), actual.getName());
        assertEquals(requestDto.getDescription(), actual.getDescription());
        assertEquals(requestDto.getStartDate(), actual.getStartDate());
        assertEquals(requestDto.getEndDate(), actual.getEndDate());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("update a project by id")
    public void updateProject_success() throws Exception{
        UpdateProjectRequestDto requestDto = createUpdateProjectRequestDto("IN_PROGRESS");

        MvcResult mvcResult = mockMvc
                .perform(put("/projects/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        ProjectDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);

        assertEquals(requestDto.getStatus(), actual.getStatus());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("delete a project by id")
    void deleteProject_success() throws Exception{
        mockMvc.perform(delete("/labels/{id}", 1L))
                .andExpect(status().isNoContent());

    }
}
