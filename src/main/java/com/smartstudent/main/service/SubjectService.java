package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.SubjectDTO;
import com.smartstudent.main.dto.response.SubjectResponseDTO;

import java.util.List;

public interface SubjectService {
    SubjectResponseDTO createSubject(SubjectDTO dto);
    List<SubjectResponseDTO> getAllSubjects();
    SubjectResponseDTO updateSubject(Long id, SubjectDTO dto);
    void deleteSubject(Long id);
    List<SubjectResponseDTO> assignSubjectsToStudent(Long studentId, List<SubjectDTO> subjects);
    List<SubjectResponseDTO> getStudentSubjects(Long studentId);
}
