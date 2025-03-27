package com.example.rental.file;


import com.example.rental.file.UploadFileResponse;
import com.example.rental.config.FeignConfig;
import com.example.rental.file.Fallbacks.FileServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.util.List;

@FeignClient(name = "FILE-SERVICE", configuration = FeignConfig.class,fallback = FileServiceClientFallback.class)
public interface FileServiceClient {

    // Эндпоинты FileController

    @PostMapping(value = "/api/files/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UploadFileResponse uploadFile(@RequestPart("file") MultipartFile file,
                                  @RequestHeader("Authorization") String authHeader);

    @PostMapping(value = "/api/files/uploadMultipleFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<UploadFileResponse> uploadMultipleFiles(@RequestPart("files") MultipartFile[] files,
                                                 @RequestHeader("Authorization") String authHeader);

    @GetMapping("/api/files/downloadFile/{fileName:.+}")
    ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName,
                                          @RequestHeader("Authorization") String authHeader);

    @DeleteMapping("/api/files/deleteFile/{fileName}")
    ResponseEntity<Void> deleteFile(@PathVariable("fileName") String fileName,
                                    @RequestHeader("Authorization") String authHeader);

    @PostMapping("/api/files/downloadMultipleFiles")
    ResponseEntity<List<Resource>> downloadMultipleFiles(@RequestParam("fileNames") List<String> fileNames,
                                                         @RequestHeader("Authorization") String authHeader);

    @GetMapping("/api/files/getFileDownloadUri/{fileName}")
    String getFileDownloadUri(@PathVariable("fileName") String fileName);


}