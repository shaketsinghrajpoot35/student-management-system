package com.smartstudent.main.dto.request;

import com.smartstudent.main.enums.BloodGroup;
import com.smartstudent.main.enums.Category;
import com.smartstudent.main.enums.Gender;
import com.smartstudent.main.enums.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalInfoDTO {

    @NotBlank(message = "Samagra ID is required")
    @Pattern(regexp = "^[0-9]{9}$", message = "Samagra ID must be exactly 9 digits")
    private String samagraId;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private BloodGroup bloodGroup;
    private Category category;

    @Size(max = 50)
    private String religion;

    @Size(max = 50)
    private String nationality;

    @Size(max = 100)
    private String fatherName;

    @Size(max = 100)
    private String motherName;

    @Size(max = 100)
    private String guardianName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid mobile number")
    private String mobileNumber;

    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid alternate mobile number")
    private String alternateMobileNumber;

    @Email(message = "Invalid email address")
    private String email;

    @Size(max = 255)
    private String address;

    @Size(max = 60)
    private String city;

    @Size(max = 60)
    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode")
    private String pincode;

    private LocalDate admissionDate;

    private StudentStatus studentStatus;
}
