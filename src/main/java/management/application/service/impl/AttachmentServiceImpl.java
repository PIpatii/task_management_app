package management.application.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import management.application.dto.attachment.AttachmentDto;
import management.application.mapper.AttachmentMapper;
import management.application.model.Attachment;
import management.application.repository.AttachmentRepository;
import management.application.service.AttachmentService;
import management.application.service.dropbox.DropBoxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final DropBoxService dropBoxService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    @Transactional
    public AttachmentDto createAttachment(MultipartFile file, Long taskId) {
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setTaskId(taskId);
        attachment.setContentType(file.getContentType());
        attachment.setDropboxFileId(dropBoxService.uploadFile(file));
        attachment.setUploadTime(LocalDateTime.now());

        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public Page<AttachmentDto> getAttachments(Long taskId, Pageable pageable) {
        Page<Attachment> attachments = attachmentRepository
                .getAttachmentsByTaskId(taskId, pageable);
        return attachments.map(attachmentMapper::toDto);
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Attachment not found"));

        String filename = attachment.getFileName() != null ? attachment.getFileName() : "file.bin";
        response.setContentType(attachment.getContentType());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"");

        try (OutputStream output = response.getOutputStream()) {
            dropBoxService.downloadFile(attachment.getDropboxFileId(), output);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file from DropeBox", e);
        }
    }

    @Override
    @Transactional
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Attachment not found"));
        dropBoxService.deleteFile(attachment.getDropboxFileId());
        attachmentRepository.deleteById(attachment.getId());
    }
}
