package com.smartstudent.main.dto.response;

import com.smartstudent.main.enums.DocumentType;
import com.smartstudent.main.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDTO {
    private Long id;
    private DocumentType documentType;
    private String documentName;
    private String documentNumber;
    private String fileName;
    private LocalDateTime uploadDate;
    private VerificationStatus verificationStatus;
    private String remarks;
    private Long studentId;
}
