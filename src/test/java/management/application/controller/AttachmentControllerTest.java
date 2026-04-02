package management.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.SneakyThrows;
import management.application.dto.attachment.AttachmentDto;
import management.application.service.dropbox.DropBoxService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class AttachmentControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DropBoxService dropBoxService;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource,
                           @Autowired WebApplicationContext webApplicationContext) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/attachment/add-two-elements-to-attachments-table.sql"));
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
                    new ClassPathResource("database/attachment/remove-all-elements-from-attachments-table.sql"));
        }
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get all task's attachments")
    public void getAttachments_success() throws Exception {
        int expectedSize = 2;

        MvcResult mvcResult = mockMvc.perform(
                        get("/attachments")
                                .param("taskId", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        AttachmentDto[] actual = objectMapper.readValue(
                root.get("content").toString(), AttachmentDto[].class);

        assertEquals(expectedSize, actual.length);

    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("download a file from dropbox")
    public void downloadFile_success() throws Exception {
        byte[] fileBytes = "Hello world".getBytes();

        doAnswer(invocation -> {
            OutputStream os = invocation.getArgument(1);
            os.write(fileBytes);
            return null;
        }).when(dropBoxService).downloadFile(eq("id:123456"), any());

        mockMvc.perform(get("/attachments/" + 1 + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"test.txt\""))
                .andExpect(content().bytes(fileBytes));

    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("create an attachment and upload a file to dropbox")
    public void createAttachment_success() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello world".getBytes()
        );
        when(dropBoxService.uploadFile(file)).thenReturn("id:123456");

        mockMvc.perform(multipart("/attachments")
                        .file(file)
                        .param("taskId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.contentType").value("text/plain"));
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("delete an attachment by id")
    public void deleteAttachments_success() throws Exception {
        doNothing().when(dropBoxService)
                .deleteFile("id:123456");

        mockMvc.perform(delete("/attachments/{id}" ,1L ))
                .andExpect(status().isNoContent());
    }
}
