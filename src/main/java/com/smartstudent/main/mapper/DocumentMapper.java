package com.smartstudent.main.mapper;

import com.smartstudent.main.dto.request.DocumentDTO;
import com.smartstudent.main.dto.response.DocumentResponseDTO;
import com.smartstudent.main.entity.StudentDocument;
import com.smartstudent.main.enums.VerificationStatus;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public StudentDocument toEntity(DocumentDTO dto) {
        if (dto == null) return null;
        return StudentDocument.builder()
                .documentType(dto.getDocumentType())
                .documentName(dto.getDocumentName())
                .documentNumber(dto.getDocumentNumber())
                .remarks(dto.getRemarks())
                .verificationStatus(dto.getVerificationStatus() != null ?
                        dto.getVerificationStatus() : VerificationStatus.PENDING)
                .build();
    }

    public DocumentResponseDTO toResponseDTO(StudentDocument entity) {
        if (entity == null) return null;
        return DocumentResponseDTO.builder()
                .id(entity.getId())
                .documentType(entity.getDocumentType())
                .documentName(entity.getDocumentName())
                .documentNumber(entity.getDocumentNumber())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .uploadDate(entity.getUploadDate())
                .verificationStatus(entity.getVerificationStatus())
                .remarks(entity.getRemarks())
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .build();
    }
}
