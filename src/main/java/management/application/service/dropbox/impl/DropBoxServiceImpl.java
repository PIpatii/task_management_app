package management.application.service.dropbox.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.DownloadBuilder;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.OutputStream;
import management.application.properties.DropBoxProperties;
import management.application.requester.JavaHttpClientRequestor;
import management.application.service.dropbox.DropBoxService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DropBoxServiceImpl implements DropBoxService {
    private final DbxClientV2 client;

    public DropBoxServiceImpl(DropBoxProperties props) {
        DbxRequestConfig config =
                DbxRequestConfig.newBuilder("task-app")
                        .withHttpRequestor(new JavaHttpClientRequestor())
                        .build();

        DbxCredential credential = new DbxCredential(
                "",
                0L,
                props.getRefreshToken(),
                props.getAppKey(),
                props.getAppSecret()
        );

        this.client = new DbxClientV2(config, credential);
    }

    private DbxClientV2 getClient() {
        return client;
    }

    public String uploadFile(MultipartFile file) {
        DbxClientV2 client = getClient();
        try {
            String dropboxPath = "/attachments/" + file.getOriginalFilename();

            FileMetadata metadata = client.files()
                    .uploadBuilder(dropboxPath)
                    .uploadAndFinish(file.getInputStream());
            return metadata.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file from Dropbox", e);
        }
    }

    public void downloadFile(String fileId, OutputStream output) {
        DbxClientV2 client = getClient();
        try {
            DownloadBuilder downloader =
                    client.files().downloadBuilder(fileId);
            downloader.download(output);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from Dropbox: " + fileId, e);
        }
    }

    public void deleteFile(String fileId) {
        DbxClientV2 client = getClient();
        try {
            client.files().deleteV2(fileId);
        } catch (DeleteErrorException e) {
            if (e.errorValue.isPathLookup()
                    && e.errorValue.getPathLookupValue().isNotFound()) {

                return;
            }
            throw new RuntimeException("Failed to delete file from Dropbox: " + fileId, e);
        } catch (DbxException e) {
            throw new RuntimeException("Dropbox delete error", e);
        }
    }
}
