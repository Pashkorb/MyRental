package com.example.filedemo.controller;


import com.example.filedemo.JWT_Parse;
import com.example.filedemo.service.FileStorageService;
import com.example.filedemo.service.TempLinkService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.UUID;

@RequestMapping("/api/temp-files")
public class TempFileController {

    private static final Logger logger = LoggerFactory.getLogger(TempFileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TempLinkService tempLinkService;

    @PostMapping("/generate-link")
    public ResponseEntity<String> generateTempLink(
            @RequestParam String fileName,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = JWT_Parse.getUserId(authHeader);
        String tempLink = tempLinkService.generateTempLink(userId, fileName);
        return ResponseEntity.ok(tempLink);
    }

    @GetMapping("/download/{token}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String token,
            HttpServletRequest request) {

        TempLinkService.TempFileData fileData = tempLinkService.validateTempLink(token);
        if (fileData == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = fileStorageService.loadFileAsResource(fileData.getFileName());

        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

