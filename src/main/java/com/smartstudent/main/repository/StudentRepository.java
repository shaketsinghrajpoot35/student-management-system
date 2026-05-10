package com.smartstudent.main.repository;

import com.smartstudent.main.entity.Student;
import com.smartstudent.main.enums.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findBySamagraId(String samagraId);

    boolean existsBySamagraId(String samagraId);

    @Query("""
            SELECT s FROM Student s
            LEFT JOIN s.academicDetails ad
            WHERE (:name IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:samagraId IS NULL OR s.samagraId = :samagraId)
              AND (:className IS NULL OR ad.className = :className)
              AND (:rollNumber IS NULL OR ad.rollNumber = :rollNumber)
              AND (:admissionNumber IS NULL OR ad.admissionNumber = :admissionNumber)
              AND (:stream IS NULL OR ad.stream = :stream)
            """)
    Page<Student> searchStudents(
            @Param("name") String name,
            @Param("samagraId") String samagraId,
            @Param("className") String className,
            @Param("rollNumber") String rollNumber,
            @Param("admissionNumber") String admissionNumber,
            @Param("stream") Stream stream,
            Pageable pageable
    );
}
