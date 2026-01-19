package gr.hua.dit.mycitygov.core.port;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String uploadFile(MultipartFile file);
    String getFileUrl(String fileName);

}