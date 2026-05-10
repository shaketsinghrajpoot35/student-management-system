package com.smartstudent.main.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartstudent.main.dto.StudentRequestDTO;
import com.smartstudent.main.entity.Student;
import com.smartstudent.main.repository.StudentRepository;
import com.smartstudent.main.service.StudentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Student createStudent(StudentRequestDTO dto) {

        Student student = Student.builder()
                .samagraId(dto.getSamagraId())
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .fatherName(dto.getFatherName())
                .motherName(dto.getMotherName())
                .mobileNumber(dto.getMobileNumber())
                .address(dto.getAddress())
                .build();

        return studentRepository.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));
    }

    @Override
    public Student updateStudent(Long id, StudentRequestDTO dto) {

        Student student = getStudentById(id);

        student.setFullName(dto.getFullName());
        student.setGender(dto.getGender());
        student.setFatherName(dto.getFatherName());
        student.setMotherName(dto.getMotherName());
        student.setMobileNumber(dto.getMobileNumber());
        student.setAddress(dto.getAddress());

        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {

        Student student = getStudentById(id);

        studentRepository.delete(student);
    }
}