package com.smartstudent.main.dto.request;

import com.smartstudent.main.enums.Stream;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AcademicInfoDTO {

    @NotBlank(message = "Class name is required")
    @Size(max = 20)
    private String className;

    @Size(max = 5)
    private String section;

    @Size(max = 20)
    private String rollNumber;

    @Size(max = 30)
    private String admissionNumber;

    @Size(max = 50)
    private String board;

    @Size(max = 20)
    private String academicYear;

    @Size(max = 100)
    private String previousSchool;

    @DecimalMin(value = "0.0", message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100")
    private Double previousPercentage;

    private Stream stream;
}
