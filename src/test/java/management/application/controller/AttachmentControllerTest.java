package management.application.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import management.application.dto.attachment.AttachmentDto;
import management.application.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class AttachmentControllerTest {
    protected static MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @BeforeEach
    public void beforeEach(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get all task's attachments")
    public void getAttachments_success() throws Exception {
        AttachmentDto firstAttachment = new AttachmentDto();
        firstAttachment.setId(1L);
        firstAttachment.setFileName("file1");

        AttachmentDto secondAttachment = new AttachmentDto();
        secondAttachment.setId(2L);
        secondAttachment.setFileName("file2");


        Page<AttachmentDto> page = new PageImpl<>(Arrays.asList(firstAttachment, secondAttachment));

        when(attachmentService.getAttachments(eq(10L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/attachments")
                        .param("taskId", "10")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(attachmentService).getAttachments(eq(10L), any(Pageable.class));
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("download a file from dropbox")
    public void downloadFile_success() throws Exception {
        mockMvc.perform(get("/attachments/{id}/download", 5L))
                .andExpect(status().isOk());

        verify(attachmentService).downloadFile(eq(5L), any(HttpServletResponse.class));
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("create an attachment and upload a file to dropbox")
    public void createAttachment_success() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        AttachmentDto dto = new AttachmentDto();
        dto.setId(1L);
        dto.setFileName("test.txt");

        when(attachmentService.createAttachment(any(MultipartFile.class), eq(5L)))
                .thenReturn(dto);

        mockMvc.perform(multipart("/attachments")
                        .file(file)
                        .param("taskId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("test.txt"));

        verify(attachmentService).createAttachment(any(MultipartFile.class), eq(5L));
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("delete an attachment by id")
    public void deleteAttachments_success() throws Exception {
        mockMvc.perform(delete("/attachments/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(attachmentService).deleteAttachment(1L);
    }
}
