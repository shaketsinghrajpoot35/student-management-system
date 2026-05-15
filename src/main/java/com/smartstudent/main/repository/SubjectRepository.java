package com.smartstudent.main.repository;

import com.smartstudent.main.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCodeAndAdmin(String subjectCode, com.smartstudent.main.entity.Admin admin);
    Optional<Subject> findBySubjectNameAndAdmin(String subjectName, com.smartstudent.main.entity.Admin admin);
    boolean existsBySubjectCodeAndAdmin(String subjectCode, com.smartstudent.main.entity.Admin admin);
    java.util.List<Subject> findAllByAdmin(com.smartstudent.main.entity.Admin admin);
    Optional<Subject> findByIdAndAdmin(Long id, com.smartstudent.main.entity.Admin admin);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(sb) FROM Subject sb WHERE sb.admin.schoolCode = :schoolCode")
    long countBySchoolCode(@org.springframework.data.repository.query.Param("schoolCode") String schoolCode);
}
