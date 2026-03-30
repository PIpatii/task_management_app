package management.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import management.application.dto.comment.AddCommentRequestDto;
import management.application.dto.comment.CommentDto;
import management.application.helper.TestDataHelper;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class CommentControllerTest {
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
                    new ClassPathResource("database/comment/add-two-elements-to-comments-table.sql"));
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
                    new ClassPathResource("database/comment/remove-all-elements-from-comments-table.sql"));
        }
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get task's comments by task id")
    public void getCommentsByTaskId_success() throws  Exception {
        int expectedSize = 2;

        MvcResult mvcResult = mockMvc.perform(
                        get("/comments")
                                .param("taskId", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        CommentDto[] actual = objectMapper.readValue(
                root.get("content").toString(), CommentDto[].class);

        assertEquals(expectedSize, actual.length);
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("add comment to a task")
    public void addComment_success() throws Exception {
        AddCommentRequestDto request = TestDataHelper.createAddCommentRequestDto(1L, "Hello world");

        MvcResult mvcResult = mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        CommentDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CommentDto.class);

        assertEquals(request.getText(), actual.getText());
        assertEquals(request.getTaskId(), actual.getTaskId());
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("delete a comment by id")
    public void deleteCommentById_success() throws Exception {
        mockMvc.perform(delete("/comments/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
