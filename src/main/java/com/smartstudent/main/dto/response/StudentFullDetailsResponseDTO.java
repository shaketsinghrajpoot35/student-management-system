package com.smartstudent.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFullDetailsResponseDTO {
    private Long id;
    private StudentResponseDTO personalInfo;
    private AcademicDetailsResponseDTO academicDetails;
    private List<SubjectResponseDTO> subjects;
    private List<DocumentResponseDTO> documents;
    private BankDetailsResponseDTO bankDetails;
}
