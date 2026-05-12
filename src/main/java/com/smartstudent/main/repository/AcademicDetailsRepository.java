package com.smartstudent.main.repository;

import com.smartstudent.main.entity.AcademicDetails;
import com.smartstudent.main.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicDetailsRepository extends JpaRepository<AcademicDetails, Long> {
    Optional<AcademicDetails> findByStudentId(Long studentId);
    boolean existsByRollNumber(String rollNumber);
    boolean existsByAdmNoHashAndStudentAdmin(String admNoHash, Admin admin);
    boolean existsByAdmNoHashAndStudentAdminAndStudentIdNot(String admNoHash, Admin admin, Long studentId);
}
