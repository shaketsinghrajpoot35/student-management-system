package com.smartstudent.main.service;

import java.util.List;

import com.smartstudent.main.dto.StudentRequestDTO;
import com.smartstudent.main.entity.Student;

public interface StudentService {

    Student createStudent(StudentRequestDTO dto);

    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student updateStudent(Long id, StudentRequestDTO dto);

    void deleteStudent(Long id);
}