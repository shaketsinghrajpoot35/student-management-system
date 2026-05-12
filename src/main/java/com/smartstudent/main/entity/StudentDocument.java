package com.smartstudent.main.entity;

import com.smartstudent.main.enums.DocumentType;
import com.smartstudent.main.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentType documentType;

    @Column(length = 100)
    private String documentName;

    @Column(length = 255)
    @jakarta.persistence.Convert(converter = com.smartstudent.main.util.CryptoConverter.class)
    private String documentNumber;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Lob
    @Column(name = "encrypted_data", columnDefinition = "LONGBLOB")
    private byte[] encryptedData;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
