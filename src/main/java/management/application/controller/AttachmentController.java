package management.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import management.application.dto.attachment.AttachmentDto;
import management.application.service.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "attachment", description = "attachments controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/download")
    @Operation(summary = "download file", description = "download a file attachment")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        attachmentService.downloadFile(id, response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "get attachments", description = "get attachments")
    public Page<AttachmentDto> getAttachments(@RequestParam Long taskId, Pageable pageable) {
        return attachmentService.getAttachments(taskId, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "create attachment", description = "create an attachment")
    public AttachmentDto createAttachment(
            @RequestParam("taskId") Long taskId,
            @RequestParam("file") MultipartFile file) {
        return attachmentService.createAttachment(file, taskId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "delete attachment", description = "delete attachment")
    public void deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
    }
}
