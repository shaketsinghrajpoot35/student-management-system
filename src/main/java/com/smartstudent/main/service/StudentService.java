package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.StudentRegistrationRequestDTO;
import com.smartstudent.main.dto.request.StudentUpdateRequestDTO;
import com.smartstudent.main.dto.response.PagedResponseDTO;
import com.smartstudent.main.dto.response.StudentFullDetailsResponseDTO;
import com.smartstudent.main.dto.response.StudentResponseDTO;
import com.smartstudent.main.enums.Stream;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {

    StudentResponseDTO registerStudent(StudentRegistrationRequestDTO request,
                                      List<MultipartFile> files);

    StudentResponseDTO getStudentById(Long id);

    StudentFullDetailsResponseDTO getFullStudentDetails(Long id);

    StudentResponseDTO updateStudent(Long id, StudentUpdateRequestDTO request,
                                    List<MultipartFile> files);

    void deleteStudent(Long id);

    PagedResponseDTO<StudentResponseDTO> searchStudents(
            String name, String samagraId, String className,
            String rollNumber, String admissionNumber, Stream stream,
            int page, int size, String sortBy, String sortDir);
}
