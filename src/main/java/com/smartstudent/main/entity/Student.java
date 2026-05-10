package com.smartstudent.main.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String samagraId;

    private String fullName;

    private String gender;

    private LocalDate dateOfBirth;

    private String bloodGroup;

    private String category;

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

    private String studentStatus;
}