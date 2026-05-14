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


    @Value("${app.security.file-key}")
    private String fileKeyBase64;

    private byte[] getFileKey() {
        if (fileKeyBase64 == null) return new byte[32]; // Fallback
        return Base64.getDecoder().decode(fileKeyBase64.trim());
    }


    /**
     * Encrypt a byte array and prepend the IV.
     */
    public byte[] encryptToByteArray(byte[] data) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(getFileKey(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] encrypted = cipher.doFinal(data);
            
            // Combine IV + Encrypted Data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return combined;
        } catch (Exception e) {
            throw new FileStorageException("Encryption failed", e);
        }
    }

    /**
     * Decrypt a byte array (expecting IV at the beginning).
     */
    public byte[] decryptFromByteArray(byte[] encryptedDataWithIv) {
        try {
            if (encryptedDataWithIv == null || encryptedDataWithIv.length < GCM_IV_LENGTH) {
                throw new FileStorageException("Invalid encrypted data");
            }

            // Extract IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(encryptedDataWithIv, 0, iv, 0, GCM_IV_LENGTH);

            // Extract Encrypted Content
            int encryptedSize = encryptedDataWithIv.length - GCM_IV_LENGTH;
            byte[] encrypted = new byte[encryptedSize];
            System.arraycopy(encryptedDataWithIv, GCM_IV_LENGTH, encrypted, 0, encryptedSize);

            // Decryption setup
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(getFileKey(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new FileStorageException("Decryption failed", e);
        }
    }

    public void validateFile(MultipartFile file) {
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
