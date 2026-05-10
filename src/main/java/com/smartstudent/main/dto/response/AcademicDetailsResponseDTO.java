package com.smartstudent.main.dto.response;

import com.smartstudent.main.enums.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicDetailsResponseDTO {
    private Long id;
    private String className;
    private String section;
    private String rollNumber;
    private String admissionNumber;
    private String board;
    private String academicYear;
    private String previousSchool;
    private Double previousPercentage;
    private Stream stream;
}
