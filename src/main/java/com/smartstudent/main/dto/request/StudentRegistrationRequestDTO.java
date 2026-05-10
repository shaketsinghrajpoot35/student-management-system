package com.smartstudent.main.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudentRegistrationRequestDTO {

    @NotNull(message = "Personal information is required")
    @Valid
    private PersonalInfoDTO personalInfo;

    @Valid
    private AcademicInfoDTO academicInfo;

    @Valid
    private List<SubjectDTO> subjects = new ArrayList<>();

    @Valid
    private List<DocumentDTO> documents = new ArrayList<>();

    @Valid
    private BankDetailsDTO bankDetails;
}
