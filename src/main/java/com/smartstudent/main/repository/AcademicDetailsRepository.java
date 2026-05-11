package com.smartstudent.main.repository;

import com.smartstudent.main.entity.AcademicDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicDetailsRepository extends JpaRepository<AcademicDetails, Long> {
    Optional<AcademicDetails> findByStudentId(Long studentId);
    boolean existsByRollNumber(String rollNumber);
    boolean existsByAdmissionNumberHash(String admissionNumberHash);
    boolean existsByRollNumberAndStudentIdNot(String rollNumber, Long studentId);
    boolean existsByAdmissionNumberHashAndStudentIdNot(String admissionNumberHash, Long studentId);
}
