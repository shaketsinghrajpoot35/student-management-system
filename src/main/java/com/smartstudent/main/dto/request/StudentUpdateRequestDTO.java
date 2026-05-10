package com.smartstudent.main.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudentUpdateRequestDTO {

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
