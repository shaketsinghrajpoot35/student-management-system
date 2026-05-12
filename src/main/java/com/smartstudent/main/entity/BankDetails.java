package com.smartstudent.main.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String bankName;

    @Column(length = 100)
    private String branchName;

    @Column(length = 255)
    @jakarta.persistence.Convert(converter = com.smartstudent.main.util.CryptoConverter.class)
    private String ifscCode;

    @Column(length = 255)
    @jakarta.persistence.Convert(converter = com.smartstudent.main.util.CryptoConverter.class)
    private String accountNumber;

    @Column(length = 100)
    private String accountNumberHash;

    @PrePersist
    @PreUpdate
    private void updateHash() {
        if (this.accountNumber != null) {
            this.accountNumberHash = com.smartstudent.main.util.EncryptionUtil.hashForSearch(this.accountNumber);
        }
    }

    @Column(length = 100)
    private String accountHolderName;

    @Column(length = 500)
    private String passbookFilePath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;
}
