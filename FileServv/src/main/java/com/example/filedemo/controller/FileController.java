package com.example.filedemo.controller;

import com.example.filedemo.JWT_Parse;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Value("${server.port}")
    private String serverPort;
    @Autowired
    private FileStorageService fileStorageService;

    @DeleteMapping("/deleteFile/{fileName}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String fileName,
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = JWT_Parse.getUserId(authHeader);
        fileStorageService.deleteFile(fileName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getFileDownloadUri/{fileName}")
    public String getFileDownloadUri(@PathVariable String fileName) {
        return "http://" + getHostName() + ":" + serverPort + "/downloadFile/" + fileName;
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = JWT_Parse.getUserId(authHeader);
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
            @RequestHeader("Authorization") String authHeader) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file,authHeader))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request) {
        UUID userId=JWT_Parse.getUserId(authHeader);
        // Проверяем, что запрашивающий пользователь совпадает с userId
        UUID currentUserId = JWT_Parse.getUserId(authHeader);

        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @PostMapping("/downloadMultipleFiles")
    public ResponseEntity<List<Resource>> downloadMultipleFiles(@RequestParam List<String> fileNames, HttpServletRequest request,
                                                                @RequestHeader("Authorization") String authHeader) {
        List<Resource> resources = fileStorageService.loadAllFiles(fileNames,authHeader);

        // Определяем тип содержимого
        String contentType = "application/octet-stream"; // По умолчанию

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(resources);
    }
}
