package com.smartstudent.main.repository;

import com.smartstudent.main.entity.AcademicDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicDetailsRepository extends JpaRepository<AcademicDetails, Long> {
    Optional<AcademicDetails> findByStudentId(Long studentId);
    boolean existsByRollNumber(String rollNumber);
    boolean existsByAdmissionNumberHashAndStudentAdmin(String hash, com.smartstudent.main.entity.Admin admin);
    boolean existsByAdmissionNumberHashAndStudentAdminAndStudentIdNot(String hash, com.smartstudent.main.entity.Admin admin, Long studentId);
}
