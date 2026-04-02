package management.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import management.application.dto.task.CreateTaskRequestDto;
import management.application.dto.task.TaskDto;
import management.application.dto.task.UpdateTaskRequestDto;
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
import static management.application.helper.TestDataHelper.createCreateTaskRequestDto;
import static management.application.helper.TestDataHelper.createTaskDto;
import static management.application.helper.TestDataHelper.createUpdateTaskRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class TaskControllerTest {
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
                    new ClassPathResource("database/task/add-two-elements-to-tasks-table.sql"));
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
                    new ClassPathResource("database/task/remove-all-elements-from-tasks-table.sql"));
        }
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get all project's tasks")
    public void getTasks_success() throws Exception {
        int expectedSize = 1;

        MvcResult mvcResult = mockMvc.perform(
                        get("/tasks")
                                .param("projectId", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        TaskDto[] actual = objectMapper.readValue(
                root.get("content").toString(), TaskDto[].class);

        assertEquals(expectedSize, actual.length);
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get a task by id")
    public void getTaskById_success() throws Exception {
        TaskDto expected = createTaskDto(1L, "task", "description",
                "IN_PROGRESS", LocalDate.of(2026, 3, 21),
                "LOW", 1L, 2L);

        MvcResult mvcResult = mockMvc
                .perform(get("/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        TaskDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TaskDto.class);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getDeadline(), actual.getDeadline());
        assertEquals(expected.getProjectId(), actual.getProjectId());
        assertEquals(expected.getAssigneeId(), actual.getAssigneeId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getPriority(), actual.getPriority());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("create a task")
    public void createTask_success() throws Exception {
        CreateTaskRequestDto requestDto = createCreateTaskRequestDto("task", "description",
                LocalDate.now().plusMonths(1), "LOW", 1L, 2L, Set.of(1L));

        MvcResult mvcResult = mockMvc
                .perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        TaskDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TaskDto.class);

        assertEquals(requestDto.getAssigneeId(), actual.getAssigneeId());
        assertEquals(requestDto.getProjectId(), actual.getProjectId());
        assertEquals(requestDto.getName(), actual.getName());
        assertEquals(requestDto.getDescription(), actual.getDescription());
        assertEquals(requestDto.getDeadline(), actual.getDeadline());
        assertEquals(requestDto.getPriority(), actual.getPriority());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("update a task by id")
    public void updateTask_success() throws Exception {
        UpdateTaskRequestDto requestDto = createUpdateTaskRequestDto("IN_PROGRESS", "LOW");

        MvcResult mvcResult = mockMvc
                .perform(put("/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        TaskDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TaskDto.class);

        assertEquals(requestDto.getStatus(), actual.getStatus());
        assertEquals(requestDto.getPriority(), actual.getPriority());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("delete a task by id")
    public void deleteTask_success() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
