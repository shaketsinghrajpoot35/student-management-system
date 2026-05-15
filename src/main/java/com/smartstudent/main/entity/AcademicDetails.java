package com.smartstudent.main.entity;

import com.smartstudent.main.enums.Stream;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "academic_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String className;

    @Column(length = 5)
    private String section;

    @Column(length = 20)
    private String rollNumber;

    @Column(length = 255, nullable = false)
    @jakarta.persistence.Convert(converter = com.smartstudent.main.util.CryptoConverter.class)
    private String admissionNumber;

    @Column(length = 50)
    private String board;

    @Column(length = 20)
    private String academicYear;

    @Column(length = 100)
    private String previousSchool;

    private Double previousPercentage;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Stream stream;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }
}
