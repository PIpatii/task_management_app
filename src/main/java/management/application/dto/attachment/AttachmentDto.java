package management.application.dto.attachment;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentDto {
    private Long id;
    private Long taskId;
    private String fileName;
    private String dropboxFileId;
    private LocalDateTime uploadTime;
    private String contentType;
}
