package com.smartstudent.main.util;

import com.smartstudent.main.entity.AcademicDetails;
import com.smartstudent.main.entity.Student;
import com.smartstudent.main.repository.AcademicDetailsRepository;
import com.smartstudent.main.repository.BankDetailsRepository;
import com.smartstudent.main.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    @Transactional
    public void fixSearchIndexes() {
        log.info("Starting database schema and index synchronization...");

        // Fix: Manual creation of join table if Hibernate missed it
        try {
            log.info("Checking for missing join table 'student_subjects'...");
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS student_subjects (
                    student_id BIGINT NOT NULL,
                    subject_id BIGINT NOT NULL,
                    PRIMARY KEY (student_id, subject_id),
                    CONSTRAINT FK_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                    CONSTRAINT FK_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """);
            log.info("Join table 'student_subjects' is verified/created.");
        } catch (Exception e) {
            log.warn("Schema fix skipped (might already exist or tables missing): {}", e.getMessage());
        }

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
