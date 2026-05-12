package com.smartstudent.main.service.impl;

import com.smartstudent.main.dto.request.DocumentDTO;
import com.smartstudent.main.dto.response.DocumentResponseDTO;
import com.smartstudent.main.entity.Student;
import com.smartstudent.main.entity.StudentDocument;
import com.smartstudent.main.exception.ResourceNotFoundException;
import com.smartstudent.main.mapper.DocumentMapper;
import com.smartstudent.main.repository.StudentDocumentRepository;
import com.smartstudent.main.repository.StudentRepository;
import com.smartstudent.main.service.StudentDocumentService;
import com.smartstudent.main.util.FileStorageUtil;
import com.smartstudent.main.util.SecurityUtil;
import com.smartstudent.main.entity.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentDocumentServiceImpl implements StudentDocumentService {

    private final StudentDocumentRepository documentRepository;
    private final StudentRepository studentRepository;
    private final DocumentMapper documentMapper;
    private final FileStorageUtil fileStorageUtil;
    private final SecurityUtil securityUtil;

    @Override
    public DocumentResponseDTO uploadDocument(Long studentId, DocumentDTO dto, MultipartFile file) {
        Admin admin = securityUtil.getCurrentAdmin();
        log.info("Uploading document {} for student {}", dto.getDocumentType(), studentId);
        Student student = studentRepository.findByIdAndAdmin(studentId, admin)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        StudentDocument document = documentMapper.toEntity(dto);
        document.setStudent(student);

        if (dto.getDocumentType() == com.smartstudent.main.enums.DocumentType.PEN_NUMBER) {
            if (dto.getDocumentNumber() == null || !dto.getDocumentNumber().matches("^\\d{11}$")) {
                throw new IllegalArgumentException("PEN Number must be exactly 11 digits");
            }
        }

        if (file != null && !file.isEmpty()) {
            byte[] encryptedData = fileStorageUtil.encryptToBytes(file);
            document.setFileName(file.getOriginalFilename());
            document.setFilePath("db://" + file.getOriginalFilename()); // Virtual path
            document.setEncryptedData(encryptedData);
        }

        return documentMapper.toResponseDTO(documentRepository.save(document));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getDocumentsByStudentId(Long studentId) {
        Admin admin = securityUtil.getCurrentAdmin();
        // Verify student belongs to admin
        studentRepository.findByIdAndAdmin(studentId, admin)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        return documentRepository.findByStudentId(studentId).stream()
                .map(documentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentResponseDTO updateDocument(Long documentId, DocumentDTO dto, MultipartFile file) {
        log.info("Updating document ID: {}", documentId);
        StudentDocument document = findDocumentById(documentId);

        if (dto.getDocumentType() != null) document.setDocumentType(dto.getDocumentType());
        if (dto.getDocumentName() != null) document.setDocumentName(dto.getDocumentName());
        if (dto.getDocumentNumber() != null) document.setDocumentNumber(dto.getDocumentNumber());
        if (dto.getRemarks() != null) document.setRemarks(dto.getRemarks());
        if (dto.getVerificationStatus() != null) document.setVerificationStatus(dto.getVerificationStatus());

        // Replace data if provided
        if (file != null && !file.isEmpty()) {
            // Old file cleanup (if any existed on disk)
            if (document.getFilePath() != null && !document.getFilePath().startsWith("db://")) {
                fileStorageUtil.deleteFile(document.getFilePath());
            }
            
            byte[] encryptedData = fileStorageUtil.encryptToBytes(file);
            document.setFileName(file.getOriginalFilename());
            document.setFilePath("db://" + file.getOriginalFilename());
            document.setEncryptedData(encryptedData);
        }

        return documentMapper.toResponseDTO(documentRepository.save(document));
    }

    @Override
    public void deleteDocument(Long documentId) {
        log.info("Deleting document ID: {}", documentId);
        StudentDocument document = findDocumentById(documentId);
        fileStorageUtil.deleteFile(document.getFilePath());
        documentRepository.delete(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) {
        StudentDocument document = findDocumentById(documentId);
        
        // If it's a new DB-backed document
        if (document.getEncryptedData() != null) {
            return fileStorageUtil.decryptToResource(document.getEncryptedData(), document.getFileName());
        }
        
        // Fallback for old file-based documents
        return fileStorageUtil.loadFileAsResource(document.getFilePath());
    }

    @Override
    @Transactional(readOnly = true)
    public String getDocumentFileName(Long documentId) {
        return findDocumentById(documentId).getFileName();
    }

    @Override
    @Transactional(readOnly = true)
    public String getDocumentContentType(Long documentId) {
        String fileName = findDocumentById(documentId).getFileName();
        return fileStorageUtil.determineContentType(fileName != null ? fileName : "file.pdf");
    }

    private StudentDocument findDocumentById(Long id) {
        Admin admin = securityUtil.getCurrentAdmin();
        StudentDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        if (document.getStudent() != null && document.getStudent().getAdmin() != null &&
            !document.getStudent().getAdmin().getId().equals(admin.getId())) {
            throw new RuntimeException("Access denied to this document");
        }
        return document;
    }
}
