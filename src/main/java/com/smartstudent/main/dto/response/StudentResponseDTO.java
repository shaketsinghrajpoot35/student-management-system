package com.smartstudent.main.dto.response;

import com.smartstudent.main.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
    private Long id;
    private String samagraId;
    private String fullName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private BloodGroup bloodGroup;
    private Category category;
    private String religion;
    private String nationality;
    private String fatherName;
    private String motherName;
    private String guardianName;
    private String mobileNumber;
    private String alternateMobileNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private LocalDate admissionDate;
    private StudentStatus studentStatus;
    private String admissionNumber;
    private String photoPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Attendance Stats
    private Double attendancePercentage;
    private String attendanceSummary;
}
