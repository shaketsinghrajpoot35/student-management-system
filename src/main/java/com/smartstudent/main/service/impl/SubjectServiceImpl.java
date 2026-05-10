package com.smartstudent.main.service.impl;

import com.smartstudent.main.dto.request.SubjectDTO;
import com.smartstudent.main.dto.response.SubjectResponseDTO;
import com.smartstudent.main.entity.Student;
import com.smartstudent.main.entity.Subject;
import com.smartstudent.main.exception.DuplicateResourceException;
import com.smartstudent.main.exception.ResourceNotFoundException;
import com.smartstudent.main.mapper.SubjectMapper;
import com.smartstudent.main.repository.StudentRepository;
import com.smartstudent.main.repository.SubjectRepository;
import com.smartstudent.main.service.SubjectService;
import com.smartstudent.main.util.SecurityUtil;
import com.smartstudent.main.entity.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectMapper subjectMapper;
    private final SecurityUtil securityUtil;

    @Override
    public SubjectResponseDTO createSubject(SubjectDTO dto) {
        Admin admin = securityUtil.getCurrentAdmin();
        if (dto.getSubjectCode() != null &&
                subjectRepository.existsBySubjectCodeAndAdmin(dto.getSubjectCode(), admin)) {
            throw new DuplicateResourceException(
                    "Subject already exists with code: " + dto.getSubjectCode());
        }
        Subject subject = subjectMapper.toEntity(dto);
        subject.setAdmin(admin);
        return subjectMapper.toResponseDTO(subjectRepository.save(subject));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDTO> getAllSubjects() {
        Admin admin = securityUtil.getCurrentAdmin();
        return subjectRepository.findAllByAdmin(admin).stream()
                .map(subjectMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubjectResponseDTO> assignSubjectsToStudent(Long studentId, List<SubjectDTO> subjectDTOs) {
        Admin admin = securityUtil.getCurrentAdmin();
        Student student = studentRepository.findByIdAndAdmin(studentId, admin)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Subject> subjects = subjectDTOs.stream().map(dto -> {
            if (dto.getSubjectCode() != null) {
                return subjectRepository.findBySubjectCodeAndAdmin(dto.getSubjectCode(), admin)
                        .orElseGet(() -> {
                            Subject s = subjectMapper.toEntity(dto);
                            s.setAdmin(admin);
                            return subjectRepository.save(s);
                        });
            }
            return subjectRepository.findBySubjectNameAndAdmin(dto.getSubjectName(), admin)
                    .orElseGet(() -> {
                            Subject s = subjectMapper.toEntity(dto);
                            s.setAdmin(admin);
                            return subjectRepository.save(s);
                    });
        }).collect(Collectors.toList());

        // Merge (add new, keep existing)
        List<Subject> existing = student.getSubjects();
        subjects.forEach(s -> {
            if (!existing.contains(s)) existing.add(s);
        });
        student.setSubjects(existing);
        studentRepository.save(student);

        return existing.stream()
                .map(subjectMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDTO> getStudentSubjects(Long studentId) {
        Admin admin = securityUtil.getCurrentAdmin();
        Student student = studentRepository.findByIdAndAdmin(studentId, admin)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        return student.getSubjects().stream()
                .map(subjectMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectResponseDTO updateSubject(Long id, SubjectDTO dto) {
        Admin admin = securityUtil.getCurrentAdmin();
        log.info("Updating subject ID: {}", id);
        Subject subject = subjectRepository.findByIdAndAdmin(id, admin)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
        if (dto.getSubjectName() != null) subject.setSubjectName(dto.getSubjectName());
        if (dto.getSubjectCode() != null) subject.setSubjectCode(dto.getSubjectCode());
        if (dto.getDescription() != null) subject.setDescription(dto.getDescription());
        return subjectMapper.toResponseDTO(subjectRepository.save(subject));
    }

    @Override
    public void deleteSubject(Long id) {
        Admin admin = securityUtil.getCurrentAdmin();
        log.info("Deleting subject ID: {}", id);
        Subject subject = subjectRepository.findByIdAndAdmin(id, admin)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
        // Remove from all student associations first to avoid FK constraint
        List<Student> students = studentRepository.findAll();
        students.forEach(s -> {
            if (s.getSubjects().removeIf(sub -> sub.getId().equals(id))) {
                studentRepository.save(s);
            }
        });
        subjectRepository.delete(subject);
        log.info("Subject deleted: {}", id);
    }
}

