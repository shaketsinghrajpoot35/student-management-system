package com.smartstudent.main.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequestDTO {

    @NotBlank
    private String samagraId;

    @NotBlank
    private String fullName;

    private String gender;

    private String fatherName;

    private String motherName;

    private String mobileNumber;

    private String address;
}