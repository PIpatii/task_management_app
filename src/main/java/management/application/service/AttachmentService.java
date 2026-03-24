package management.application.service;

import jakarta.servlet.http.HttpServletResponse;
import management.application.dto.attachment.AttachmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

    AttachmentDto createAttachment(MultipartFile file, Long taskId);

    Page<AttachmentDto> getAttachments(Long taskId, Pageable pageable);

    void downloadFile(Long taskId, HttpServletResponse response);

    void deleteAttachment(Long id);
}
