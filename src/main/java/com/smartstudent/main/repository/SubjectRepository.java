package com.smartstudent.main.repository;

import com.smartstudent.main.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);
    Optional<Subject> findBySubjectName(String subjectName);
    boolean existsBySubjectCode(String subjectCode);
}
