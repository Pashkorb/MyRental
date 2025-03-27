package com.example.filedemo.service;

import com.example.filedemo.JWT_Parse;
import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found");
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found", ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.delete(filePath);
        } catch (NoSuchFileException ex) {
            throw new MyFileNotFoundException("File not found");
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file", ex);
        }
    }
    public List<Resource> loadAllFiles(List<String> fileNames, String authHeader) {

        UUID uuid= JWT_Parse.getUserId(authHeader);
        return fileNames.stream()
                .map(this::loadFileAsResource) // Загружаем файлы из общей папки
                .collect(Collectors.toList());
    }
}
