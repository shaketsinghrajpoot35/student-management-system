package com.smartstudent.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubjectDTO {

    @NotBlank(message = "Subject name is required")
    @Size(max = 100)
    private String subjectName;

    @Size(max = 20)
    private String subjectCode;

    @Size(max = 255)
    private String description;
}
