package com.smartstudent.main.repository;

import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.entity.Attendance;
import com.smartstudent.main.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentAndDate(Student student, LocalDate date);

    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);

    @Query("SELECT a FROM Attendance a WHERE a.student.admin.schoolCode = :schoolCode AND a.date = :date")
    List<Attendance> findBySchoolCodeAndDate(@Param("schoolCode") String schoolCode, @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.markedBy = :teacher AND a.date = :date")
    List<Attendance> findByTeacherAndDate(@Param("teacher") Admin teacher, @Param("date") LocalDate date);
    
    long countByStudentAndStatus(Student student, com.smartstudent.main.enums.AttendanceStatus status);
}
