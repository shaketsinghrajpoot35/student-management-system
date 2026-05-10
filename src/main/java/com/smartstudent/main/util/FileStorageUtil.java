package com.smartstudent.main.util;

import com.smartstudent.main.exception.BadRequestException;
import com.smartstudent.main.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class FileStorageUtil {

    private static final List<String> ALLOWED_CONTENT_TYPES =
            List.of("application/pdf", "image/jpeg", "image/jpg", "image/png");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10 MB

    @Value("${file.upload.dir}")
    private String uploadDir;

    /**
     * Store a file for a student document.
     * @return relative file path stored in DB
     */
    public String storeFile(MultipartFile file, Long studentId, String documentType) {
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        String uniqueFileName = documentType + "_" + UUID.randomUUID() + "." + extension;

        try {
            Path studentDir = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize()
                    .resolve("student_" + studentId);
            Files.createDirectories(studentDir);

            Path targetPath = studentDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = "student_" + studentId + "/" + uniqueFileName;
            log.info("Stored file: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + originalFilename, e);
        }
    }

    /**
     * Load a file as a downloadable Resource.
     */
    public Resource loadFileAsResource(String filePath) {
        try {
            Path fullPath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filePath);
            Resource resource = new UrlResource(fullPath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new FileStorageException("File not found or not readable: " + filePath);
        } catch (MalformedURLException e) {
            throw new FileStorageException("Invalid file path: " + filePath, e);
        }
    }

    /**
     * Delete a file from disk.
     */
    public void deleteFile(String filePath) {
        if (!StringUtils.hasText(filePath)) return;
        try {
            Path fullPath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filePath);
            boolean deleted = Files.deleteIfExists(fullPath);
            if (deleted) {
                log.info("Deleted file: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Could not delete file: {} — {}", filePath, e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty or missing");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds 10MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException(
                    "Invalid file type. Allowed: PDF, JPG, PNG. Received: " + contentType);
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0) ? filename.substring(dotIndex + 1).toLowerCase() : "bin";
    }

    public String determineContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }
}
