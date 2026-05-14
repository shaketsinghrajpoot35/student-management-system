package com.smartstudent.main.util;

import com.smartstudent.main.entity.AcademicDetails;
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

        // Fix AcademicDetails admNoSearch & admNoHash
        List<AcademicDetails> academicDetailsList = academicDetailsRepository.findAll();
        long academicCount = 0;
        for (AcademicDetails ad : academicDetailsList) {
            boolean updated = false;
            if (ad.getAdmNoSearch() == null && ad.getAdmissionNumber() != null) {
                ad.setAdmNoSearch(ad.getAdmissionNumber());
                updated = true;
            }
            if (ad.getAdmNoHash() == null && ad.getAdmissionNumber() != null) {
                ad.setAdmNoHash(com.smartstudent.main.util.EncryptionUtil.hashForSearch(ad.getAdmissionNumber()));
                updated = true;
            }
            if (updated) {
                academicDetailsRepository.save(ad);
                academicCount++;
            }
        }
        if (academicCount > 0) log.info("Synchronized {} academicDetails admission number fields", academicCount);
        
        log.info("Search index synchronization completed.");
    }
}
