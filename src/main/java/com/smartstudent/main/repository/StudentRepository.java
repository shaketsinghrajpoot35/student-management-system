package com.smartstudent.main.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.smartstudent.main.entity.Student;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findBySamagraId(String samagraId);
}