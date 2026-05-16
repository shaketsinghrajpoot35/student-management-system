package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.AttendanceRequestDTO;
import com.smartstudent.main.dto.response.AttendanceResponseDTO;
import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.entity.Attendance;
import com.smartstudent.main.entity.Student;
import com.smartstudent.main.exception.ResourceNotFoundException;
import com.smartstudent.main.repository.AttendanceRepository;
import com.smartstudent.main.repository.StudentRepository;
import com.smartstudent.main.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public void markAttendance(AttendanceRequestDTO request) {
        Admin current = securityUtil.getCurrentAdmin();
        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();

        log.info("Marking attendance for {} students on {}", request.getAttendanceList().size(), date);

        for (AttendanceRequestDTO.StudentAttendanceItem item : request.getAttendanceList()) {
            Student student = studentRepository.findById(item.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", item.getStudentId()));

            // Verify permission
            if ("ROLE_TEACHER".equals(current.getRole())) {
                if (student.getAdmin() == null || !student.getAdmin().getId().equals(current.getId())) {
                    throw new RuntimeException("Access denied: You do not own student " + student.getFullName());
                }
            } else {
                if (student.getAdmin() != null && !student.getAdmin().getSchoolCode().equals(current.getSchoolCode())) {
                    throw new RuntimeException("Access denied: Student belongs to another school");
                }
            }

            // Check if already exists (Update instead of Duplicate)
            Attendance attendance = attendanceRepository.findByStudentAndDate(student, date)
                    .orElse(new Attendance());

            attendance.setStudent(student);
            attendance.setDate(date);
            attendance.setStatus(item.getStatus());
            attendance.setMarkedBy(current);
            attendance.setRemarks(item.getRemarks());

            attendanceRepository.save(attendance);
        }
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentIdOrderByDateDesc(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByClassAndDate(String className, LocalDate date) {
        Admin current = securityUtil.getCurrentAdmin();
        List<Attendance> list;
        if ("ROLE_ADMIN".equals(current.getRole())) {
            list = attendanceRepository.findBySchoolCodeAndDate(current.getSchoolCode(), date);
        } else {
            list = attendanceRepository.findByTeacherAndDate(current, date);
        }
        
        return list.stream()
                .filter(a -> className == null || className.isEmpty() || 
                           (a.getStudent().getAcademicDetails() != null && className.equals(a.getStudent().getAcademicDetails().getClassName())))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AttendanceResponseDTO mapToDTO(Attendance a) {
        return AttendanceResponseDTO.builder()
                .id(a.getId())
                .studentId(a.getStudent().getId())
                .studentName(a.getStudent().getFullName())
                .date(a.getDate())
                .status(a.getStatus())
                .remarks(a.getRemarks())
                .markedByName(a.getMarkedBy().getFullName())
                .build();
    }
}
