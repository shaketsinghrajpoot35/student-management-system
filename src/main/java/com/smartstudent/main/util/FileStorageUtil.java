package com.smartstudent.main.util;

import com.smartstudent.main.exception.BadRequestException;
import com.smartstudent.main.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class FileStorageUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final List<String> ALLOWED_CONTENT_TYPES =
            List.of("application/pdf", "image/jpeg", "image/jpg", "image/png");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10 MB

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${app.security.file-key}")
    private String fileKeyBase64;

    private byte[] getFileKey() {
        return Base64.getDecoder().decode(fileKeyBase64);
    }

    /**
     * Store a file for a student document.
     * @return relative file path stored in DB
     */
    public String storeFile(MultipartFile file, Long studentId, String documentType) {
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID().toString() + ".enc"; // Randomized filename as per recommendation

        try {
            Path studentDir = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize()
                    .resolve("student_" + studentId);
            Files.createDirectories(studentDir);

            Path targetPath = studentDir.resolve(uniqueFileName);

            // Encryption setup
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(getFileKey(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new FileOutputStream(targetPath.toFile())) {
                
                // Write IV first
                outputStream.write(iv);

                // Then write encrypted content
                try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        cipherOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            String relativePath = "student_" + studentId + "/" + uniqueFileName;
            log.info("Stored encrypted file: {}", relativePath);
            return relativePath;

        } catch (Exception e) {
            throw new FileStorageException("Failed to store and encrypt file: " + originalFilename, e);
        }
    }

    /**
     * Encrypt a MultipartFile and return the combined IV + Encrypted Data as a byte array.
     */
    public byte[] encryptToBytes(MultipartFile file) {
        validateFile(file);
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(getFileKey(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] encryptedData = cipher.doFinal(file.getBytes());

            // Combine IV + Data
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            return combined;
        } catch (Exception e) {
            throw new FileStorageException("Failed to encrypt file data: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Delete a file from disk safely.
     */
    public void deleteFile(String filePath) {
        if (!StringUtils.hasText(filePath)) return;
        try {
            Path fullPath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filePath);
            Files.deleteIfExists(fullPath);
            log.info("Deleted file: {}", filePath);
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}. It might be locked or already removed.", filePath, e.getMessage());
        }
    }

    /**
     * Load a file as a downloadable Resource.
     */
    public Resource loadFileAsResource(String filePath) {
        try {
            Path fullPath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filePath);
            File file = fullPath.toFile();
            
            if (!file.exists()) {
                log.error("File not found on disk: {}", fullPath);
                throw new FileStorageException("Document file not found on server storage. If you recently deployed, ensure you have a Render Disk attached to persist files.");
            }
            
            if (!file.canRead()) {
                throw new FileStorageException("File is not readable: " + filePath);
            }

            byte[] key;
            try {
                key = getFileKey();
            } catch (Exception e) {
                log.error("Invalid FILE_ENCRYPTION_KEY configuration: {}", e.getMessage());
                throw new FileStorageException("Encryption key is invalid or not properly Base64 encoded. Check your environment variables.");
            }

            FileInputStream fis = new FileInputStream(file);
            
            // Read IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            int ivRead = fis.read(iv);
            if (ivRead != GCM_IV_LENGTH) {
                fis.close();
                throw new FileStorageException("Invalid encrypted file: Authentication header (IV) missing or corrupted.");
            }

            // Decryption setup
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            CipherInputStream cis = new CipherInputStream(fis, cipher);
            return new InputStreamResource(cis);

        } catch (FileStorageException e) {
            throw e;
        } catch (Exception e) {
            log.error("Decryption failed for file {}: {}", filePath, e.getMessage());
            throw new FileStorageException("Could not decrypt file. This usually happens if the FILE_ENCRYPTION_KEY has changed since the file was uploaded.", e);
        }
    }

    /**
     * Decrypt a byte array (IV + Encrypted Data) and return as a Resource.
     */
    public Resource decryptToResource(byte[] combinedData, String fileName) {
        if (combinedData == null || combinedData.length <= GCM_IV_LENGTH) {
            throw new FileStorageException("Invalid or missing encrypted data in database.");
        }

        try {
            // Extract IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combinedData, 0, iv, 0, GCM_IV_LENGTH);

            // Extract Encrypted Data
            int dataLength = combinedData.length - GCM_IV_LENGTH;
            byte[] encryptedData = new byte[dataLength];
            System.arraycopy(combinedData, GCM_IV_LENGTH, encryptedData, 0, dataLength);

            // Decryption setup
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(getFileKey(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new InputStreamResource(new ByteArrayInputStream(decryptedData));

        } catch (Exception e) {
            log.error("Decryption from database failed for {}: {}", fileName, e.getMessage());
            throw new FileStorageException("Could not decrypt document data from database.", e);
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
