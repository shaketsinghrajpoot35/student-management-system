package com.smartstudent.main.dto.request;

import com.smartstudent.main.enums.DocumentType;
import com.smartstudent.main.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentDTO {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    private String documentName;
    private String documentNumber;
    private String remarks;
    private VerificationStatus verificationStatus;
}
