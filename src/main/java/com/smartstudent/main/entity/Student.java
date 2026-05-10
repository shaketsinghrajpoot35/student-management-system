package com.smartstudent.main.entity;

import com.smartstudent.main.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, unique = true, length = 20)
    private String samagraId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Category category;

    @Column(length = 50)
    private String religion;

    @Column(length = 50)
    private String nationality;

    @Column(length = 100)
    private String fatherName;

    @Column(length = 100)
    private String motherName;

    @Column(length = 100)
    private String guardianName;

    @Column(nullable = false, length = 15)
    private String mobileNumber;

    @Column(length = 15)
    private String alternateMobileNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 60)
    private String city;

    @Column(length = 60)
    private String state;

    @Column(length = 10)
    private String pincode;

    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private StudentStatus studentStatus = StudentStatus.ACTIVE;

    @Column(length = 255)
    private String photoPath;

    // ==========================================
    // RELATIONSHIPS
    // ==========================================

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AcademicDetails academicDetails;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private BankDetails bankDetails;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<StudentDocument> documents = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_subjects",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @Builder.Default
    private List<Subject> subjects = new ArrayList<>();

    // ==========================================
    // AUDIT FIELDS
    // ==========================================

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void addDocument(StudentDocument document) {
        documents.add(document);
        document.setStudent(this);
    }

    public void removeDocument(StudentDocument document) {
        documents.remove(document);
        document.setStudent(null);
    }
}
