package management.application.service.dropbox;

import java.io.OutputStream;
import org.springframework.web.multipart.MultipartFile;

public interface DropBoxService {
    String uploadFile(MultipartFile file);

    void downloadFile(String fileId, OutputStream output);

    void deleteFile(String fileId);
}
