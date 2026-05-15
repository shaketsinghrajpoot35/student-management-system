package com.smartstudent.main.repository;

import com.smartstudent.main.entity.Admin;
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

    @Query("SELECT COUNT(s) > 0 FROM Student s WHERE s.samagraIdHash = :hash AND s.admin.schoolCode = :schoolCode")
    boolean existsBySamagraIdHashAndSchoolCode(@Param("hash") String hash, @Param("schoolCode") String schoolCode);

    @Query("SELECT COUNT(s) > 0 FROM Student s WHERE s.admNoHash = :hash AND s.admin.schoolCode = :schoolCode")
    boolean existsByAdmNoHashAndSchoolCode(@Param("hash") String hash, @Param("schoolCode") String schoolCode);

    @Query("""
            SELECT s FROM Student s
            LEFT JOIN s.academicDetails ad
            WHERE s.admin.schoolCode = :schoolCode
              AND (:name IS NULL OR :name = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:samagraId IS NULL OR :samagraId = '' OR LOWER(s.samagraIdSearch) LIKE LOWER(CONCAT('%', :samagraId, '%')))
              AND (:className IS NULL OR :className = '' OR ad.className = :className)
              AND (:rollNumber IS NULL OR :rollNumber = '' OR ad.rollNumber = :rollNumber)
              AND (:admNo IS NULL OR :admNo = '' OR LOWER(s.admNoSearch) LIKE LOWER(CONCAT('%', :admNo, '%')))
              AND (:stream IS NULL OR ad.stream = :stream)
            """)
    Page<Student> searchStudents(
            @Param("schoolCode") String schoolCode,
            @Param("name") String name,
            @Param("samagraId") String samagraId,
            @Param("className") String className,
            @Param("rollNumber") String rollNumber,
            @Param("admNo") String admNo,
            @Param("stream") Stream stream,
            Pageable pageable
    );

    boolean existsByAdmNoHashAndAdmin(String admNoHash, com.smartstudent.main.entity.Admin admin);

    @Query("SELECT COALESCE(ad.className, 'Unassigned'), COUNT(s) FROM Student s LEFT JOIN s.academicDetails ad WHERE s.admin.schoolCode = :schoolCode GROUP BY ad.className")
    java.util.List<Object[]> countStudentsByClass(@Param("schoolCode") String schoolCode);

    @Query("SELECT COALESCE(ad.stream, 'GENERAL'), COUNT(s) FROM Student s LEFT JOIN s.academicDetails ad WHERE s.admin.schoolCode = :schoolCode GROUP BY ad.stream")
    java.util.List<Object[]> countStudentsByStream(@Param("schoolCode") String schoolCode);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.admin.schoolCode = :schoolCode")
    long countBySchoolCode(@Param("schoolCode") String schoolCode);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.studentStatus = :status AND s.admin.schoolCode = :schoolCode")
    long countByStudentStatusAndSchoolCode(@Param("status") com.smartstudent.main.enums.StudentStatus status, @Param("schoolCode") String schoolCode);
    @Query("SELECT s FROM Student s WHERE " +
           "s.admin = :creator AND " +
           "(:name IS NULL OR :name = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:samagraId IS NULL OR :samagraId = '' OR s.samagraIdSearch LIKE CONCAT('%', :samagraId, '%')) AND " +
           "(:className IS NULL OR :className = '' OR s.academicDetails.className = :className) AND " +
           "(:section IS NULL OR :section = '' OR s.academicDetails.section = :section) AND " +
           "(:admNo IS NULL OR :admNo = '' OR s.admNoSearch LIKE CONCAT('%', :admNo, '%')) AND " +
           "(:stream IS NULL OR s.academicDetails.stream = :stream)")
    Page<Student> searchStudentsByCreator(
            @Param("creator") Admin creator,
            @Param("name") String name,
            @Param("samagraId") String samagraId,
            @Param("className") String className,
            @Param("section") String section,
            @Param("admNo") String admNo,
            @Param("stream") Stream stream,
            Pageable pageable);

    @Query("SELECT COALESCE(ad.className, 'Unassigned'), COUNT(s) FROM Student s LEFT JOIN s.academicDetails ad WHERE s.admin = :creator GROUP BY ad.className")
    java.util.List<Object[]> countStudentsByClassAndCreator(@Param("creator") Admin creator);

    @Query("SELECT COALESCE(ad.stream, 'GENERAL'), COUNT(s) FROM Student s LEFT JOIN s.academicDetails ad WHERE s.admin = :creator GROUP BY ad.stream")
    java.util.List<Object[]> countStudentsByStreamAndCreator(@Param("creator") Admin creator);

    long countByAdmin(Admin creator);

    long countByStudentStatusAndAdmin(com.smartstudent.main.enums.StudentStatus status, Admin creator);
}
