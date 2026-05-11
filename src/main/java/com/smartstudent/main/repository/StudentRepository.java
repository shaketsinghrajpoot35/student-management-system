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

    Optional<Student> findBySamagraIdHashAndAdmin(String samagraIdHash, com.smartstudent.main.entity.Admin admin);

    boolean existsBySamagraIdHashAndAdmin(String samagraIdHash, com.smartstudent.main.entity.Admin admin);

    Optional<Student> findByIdAndAdmin(Long id, com.smartstudent.main.entity.Admin admin);

    @Query("""
            SELECT s FROM Student s
            LEFT JOIN s.academicDetails ad
            WHERE s.admin = :admin
              AND (:name IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :name, '%')))
               AND (:samagraIdHash IS NULL OR s.samagraIdHash = :samagraIdHash)
               AND (:className IS NULL OR ad.className = :className)
               AND (:rollNumber IS NULL OR ad.rollNumber = :rollNumber)
               AND (:admissionNumberHash IS NULL OR ad.admissionNumberHash = :admissionNumberHash)
              AND (:stream IS NULL OR ad.stream = :stream)
            """)
    Page<Student> searchStudents(
            @Param("admin") com.smartstudent.main.entity.Admin admin,
            @Param("name") String name,
            @Param("samagraIdHash") String samagraIdHash,
            @Param("className") String className,
            @Param("rollNumber") String rollNumber,
            @Param("admissionNumberHash") String admissionNumberHash,
            @Param("stream") Stream stream,
            Pageable pageable
    );
}
