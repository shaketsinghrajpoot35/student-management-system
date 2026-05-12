package com.smartstudent.main.util;

import com.smartstudent.main.entity.AcademicDetails;
import com.smartstudent.main.entity.Student;
import com.smartstudent.main.repository.AcademicDetailsRepository;
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
        
        log.info("Search index synchronization completed.");
    }
}
