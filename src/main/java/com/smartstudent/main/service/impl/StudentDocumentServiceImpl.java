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

    @Override
    public DocumentResponseDTO uploadDocument(Long studentId, DocumentDTO dto, MultipartFile file) {
        log.info("Uploading document {} for student {}", dto.getDocumentType(), studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        StudentDocument document = documentMapper.toEntity(dto);
        document.setStudent(student);

        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageUtil.storeFile(
                    file, studentId, dto.getDocumentType().name());
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(filePath);
        }

        return documentMapper.toResponseDTO(documentRepository.save(document));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getDocumentsByStudentId(Long studentId) {
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

        // Replace file if provided
        if (file != null && !file.isEmpty()) {
            fileStorageUtil.deleteFile(document.getFilePath());
            String filePath = fileStorageUtil.storeFile(
                    file, document.getStudent().getId(), document.getDocumentType().name());
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(filePath);
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
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
    }
}
