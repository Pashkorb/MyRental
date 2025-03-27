package com.example.rental.file.Fallbacks;

import com.example.rental.file.UploadFileResponse;
import com.example.rental.file.FileServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;

@Component
public class FileServiceClientFallback implements FileServiceClient {

    @Override
    public UploadFileResponse uploadFile(MultipartFile file, String authHeader) {
        throw new RuntimeException("File service is unavailable");
    }

    @Override
    public List<UploadFileResponse> uploadMultipleFiles(MultipartFile[] files, String authHeader) {
        throw new RuntimeException("File service is unavailable");
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String fileName, String authHeader) {
        throw new RuntimeException("File service is unavailable");
    }

    @Override
    public ResponseEntity<Void> deleteFile(String fileName, String authHeader) {
        throw new RuntimeException("File service is unavailable");
    }

    @Override
    public ResponseEntity<List<Resource>> downloadMultipleFiles(List<String> fileNames, String authHeader) {
        throw new RuntimeException("File service is unavailable");
    }

    @Override
    public String getFileDownloadUri(String fileName) {
        throw new RuntimeException("File service is unavailable");
    }
}