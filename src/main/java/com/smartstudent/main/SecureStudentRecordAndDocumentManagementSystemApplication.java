package com.smartstudent.main;

import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SecureStudentRecordAndDocumentManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                SecureStudentRecordAndDocumentManagementSystemApplication.class, args);
    }

    /**
     * Seeds a default admin account on first run.
     * Username: admin | Password: admin@123
     */
    @Bean
    CommandLineRunner seedAdminUser(AdminRepository adminRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (!adminRepository.existsByUsername("admin")) {
                Admin admin = Admin.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin@123"))
                        .email("admin@smartstudent.com")
                        .fullName("System Administrator")
                        .role("ROLE_ADMIN")
                        .build();
                adminRepository.save(admin);
                log.info("==============================================");
                log.info("Default admin account created:");
                log.info("  Username : admin");
                log.info("  Password : admin@123");
                log.info("==============================================");
            } else {
                log.info("Admin account already exists. Skipping seed.");
            }
        };
    }
    @Bean
    CommandLineRunner migrateSearchFields(com.smartstudent.main.repository.StudentRepository studentRepository,
                                         com.smartstudent.main.repository.AcademicDetailsRepository academicRepository) {
        return args -> {
            log.info("Starting mandatory search field sync...");
            try {
                // Sync Students (including moved admission search fields)
                var allStudents = studentRepository.findAll();
                allStudents.forEach(s -> {
                    s.setSamagraIdSearch(s.getSamagraId());
                    s.setSamagraIdHash(com.smartstudent.main.util.EncryptionUtil.hashForSearch(s.getSamagraId()));
                    
                    if (s.getAcademicDetails() != null) {
                        String admNo = s.getAcademicDetails().getAdmissionNumber();
                        s.setAdmNoSearch(admNo);
                        s.setAdmNoHash(com.smartstudent.main.util.EncryptionUtil.hashForSearch(admNo));
                    }
                });
                studentRepository.saveAll(allStudents);
                log.info("Synced {} student search records and moved admission indices.", allStudents.size());
            } catch (Exception e) {
                log.error("Error during search field sync: {}", e.getMessage());
            }
            log.info("Search field sync completed.");
        };
    }
}
