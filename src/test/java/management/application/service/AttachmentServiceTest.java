package management.application.service;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import management.application.dto.attachment.AttachmentDto;
import management.application.mapper.AttachmentMapper;
import management.application.model.Attachment;
import management.application.repository.AttachmentRepository;
import management.application.service.dropbox.DropBoxService;
import management.application.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import static management.application.helper.TestDataHelper.createAttachment;
import static management.application.helper.TestDataHelper.createAttachmentDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {
    @InjectMocks
    private AttachmentServiceImpl attachmentService;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private AttachmentMapper attachmentMapper;
    @Mock
    private DropBoxService dropBoxService;

    @Test
    @DisplayName("create an attachment and upload a file to dropbox")
    public void createAttachment_correctData_success_() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getContentType()).thenReturn("management/application/pdf");

        when(dropBoxService.uploadFile(file)).thenReturn("dropbox123");

        Attachment savedAttachment = createAttachment(1L, 10L, "test.pdf", "dropbox123",
                        LocalDateTime.now(), "management/application/pdf");

        when(attachmentRepository.save(any(Attachment.class)))
                .thenReturn(savedAttachment);

        AttachmentDto expected = createAttachmentDto(1L, 10L, "test.pdf", "dropbox123",
                LocalDateTime.now(), "management/application/pdf");

        when(attachmentMapper.toDto(savedAttachment)).thenReturn(expected);

        AttachmentDto result = attachmentService.createAttachment(file, 10L);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getFileName(), result.getFileName());
        assertEquals(expected.getContentType(), result.getContentType());
        assertEquals(expected.getTaskId(), result.getTaskId());

        verify(dropBoxService).uploadFile(file);
        verify(attachmentRepository).save(any(Attachment.class));
        verify(attachmentMapper).toDto(savedAttachment);
    }

    @Test
    @DisplayName("get all attachments by task id")
    public void getAttachmentsByTaskId_success() {
        Attachment firstAttachment = createAttachment(1L, 10L, "test.pdf", "dropbox123",
                LocalDateTime.now(), "management/application/pdf");

        Attachment secondAttachment = createAttachment(2L, 10L, "test.pdf", "dropbox123",
                LocalDateTime.now(), "management/application/pdf");

        int expectedSize = 2;

        when(attachmentRepository.getAttachmentsByTaskId(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(firstAttachment, secondAttachment)));
        Page<AttachmentDto> actual = attachmentService.getAttachments(1L, PageRequest.of(0,10));

        assertEquals(expectedSize, actual.getContent().size());
    }

    @Test
    @DisplayName("download a file from Dropbox")
    public void downloadFileById_success() {
        Attachment attachment = createAttachment(1L, 10L, "test.pdf", "dropbox123",
                LocalDateTime.now(), "management/application/pdf");

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));

        MockHttpServletResponse response = new MockHttpServletResponse();

        attachmentService.downloadFile(1L, response);

        assertEquals("management/application/pdf", response.getContentType());
        assertEquals(
                "attachment; filename=\"test.pdf\"",
                response.getHeader("Content-Disposition")
        );

        verify(dropBoxService).downloadFile(eq("dropbox123"), any(OutputStream.class));
    }

    @Test
    @DisplayName("delete an attachment by id")
    public void deleteAttachment_correctData_success_() {
        Attachment attachment = createAttachment(1L, 10L, "test.pdf", "dropbox123",
                LocalDateTime.now(), "management/application/pdf");

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        doNothing().when(attachmentRepository).deleteById(1L);
        attachmentService.deleteAttachment(1L);

        verify(dropBoxService).deleteFile("dropbox123");
        verify(attachmentRepository, times(1)).deleteById(1L);
    }
}
