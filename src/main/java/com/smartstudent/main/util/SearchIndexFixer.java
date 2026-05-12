package com.smartstudent.main.util;

import com.smartstudent.main.entity.AcademicDetails;
import com.smartstudent.main.entity.BankDetails;
import com.smartstudent.main.entity.Student;
import com.smartstudent.main.repository.AcademicDetailsRepository;
import com.smartstudent.main.repository.BankDetailsRepository;
import com.smartstudent.main.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchIndexFixer {

    private final StudentRepository studentRepository;
    private final AcademicDetailsRepository academicDetailsRepository;
    private final BankDetailsRepository bankDetailsRepository;

    @PostConstruct
    @Transactional
    public void fixSearchIndexes() {
        log.info("Starting search index synchronization...");
        
        // Fix Student samagraIdSearch
        List<Student> students = studentRepository.findAll();
        long studentCount = 0;
        for (Student s : students) {
            if (s.getSamagraIdSearch() == null && s.getSamagraId() != null) {
                s.setSamagraIdSearch(s.getSamagraId());
                studentRepository.save(s);
                studentCount++;
            }
        }
        if (studentCount > 0) log.info("Synchronized {} student samagraIdSearch fields", studentCount);

        // Fix AcademicDetails admissionNumberSearch
        List<AcademicDetails> academicDetails = academicDetailsRepository.findAll();
        long academicCount = 0;
        for (AcademicDetails ad : academicDetails) {
            if (ad.getAdmissionNumberSearch() == null && ad.getAdmissionNumber() != null) {
                ad.setAdmissionNumberSearch(ad.getAdmissionNumber());
                academicDetailsRepository.save(ad);
                academicCount++;
            }
        }
        if (academicCount > 0) log.info("Synchronized {} academicDetails admissionNumberSearch fields", academicCount);

        // Fix BankDetails accountNumberHash
        List<BankDetails> bankDetails = bankDetailsRepository.findAll();
        long bankCount = 0;
        for (BankDetails bd : bankDetails) {
            if (bd.getAccountNumberHash() == null && bd.getAccountNumber() != null) {
                // The @PrePersist/@PreUpdate won't trigger on findAll unless we save
                bd.setAccountNumberHash(com.smartstudent.main.util.EncryptionUtil.hashForSearch(bd.getAccountNumber()));
                bankDetailsRepository.save(bd);
                bankCount++;
            }
        }
        if (bankCount > 0) log.info("Synchronized {} bankDetails accountNumberHash fields", bankCount);
        
        log.info("Search index synchronization completed.");
    }
}
