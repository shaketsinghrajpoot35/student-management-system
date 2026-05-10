package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.DocumentDTO;
import com.smartstudent.main.dto.response.DocumentResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentDocumentService {
    DocumentResponseDTO uploadDocument(Long studentId, DocumentDTO dto, MultipartFile file);
    List<DocumentResponseDTO> getDocumentsByStudentId(Long studentId);
    DocumentResponseDTO updateDocument(Long documentId, DocumentDTO dto, MultipartFile file);
    void deleteDocument(Long documentId);
    Resource downloadDocument(Long documentId);
    String getDocumentFileName(Long documentId);
    String getDocumentContentType(Long documentId);
}
