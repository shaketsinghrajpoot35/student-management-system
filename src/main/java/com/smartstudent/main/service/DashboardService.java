package com.smartstudent.main.service;

import com.smartstudent.main.dto.response.DashboardAnalyticsDTO;
import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.enums.StudentStatus;
import com.smartstudent.main.repository.AdminRepository;
import com.smartstudent.main.repository.StudentRepository;
import com.smartstudent.main.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AdminRepository adminRepository;
    private final com.smartstudent.main.repository.StudentDocumentRepository studentDocumentRepository;

    public DashboardAnalyticsDTO getAnalytics() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        String sc = admin.getSchoolCode();
        boolean isTeacher = "ROLE_TEACHER".equals(admin.getRole());

        long totalStudents = isTeacher ? studentRepository.countByAdmin(admin) : studentRepository.countBySchoolCode(sc);
        long activeStudents = isTeacher ? studentRepository.countByStudentStatusAndAdmin(StudentStatus.ACTIVE, admin) : studentRepository.countByStudentStatusAndSchoolCode(StudentStatus.ACTIVE, sc);
        long totalSubjects = subjectRepository.countBySchoolCode(sc); // Subjects are usually school-wide
        long totalDocuments = isTeacher ? studentDocumentRepository.countByAdmin(admin) : studentDocumentRepository.countBySchoolCode(sc);

        List<Object[]> classData = isTeacher ? studentRepository.countStudentsByClassAndCreator(admin) : studentRepository.countStudentsByClass(sc);
        Map<String, Long> studentsPerClass = new HashMap<>();
        for (Object[] row : classData) {
            String className = row[0] != null ? row[0].toString() : "Unassigned";
            Long count = ((Number) row[1]).longValue();
            studentsPerClass.put(className, count);
        }

        List<Object[]> streamData = isTeacher ? studentRepository.countStudentsByStreamAndCreator(admin) : studentRepository.countStudentsByStream(sc);
        Map<String, Long> studentsPerStream = new HashMap<>();
        for (Object[] row : streamData) {
            String streamName = row[0] != null ? row[0].toString() : "GENERAL";
            Long count = ((Number) row[1]).longValue();
            studentsPerStream.put(streamName, count);
        }

        return DashboardAnalyticsDTO.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .totalSubjects(totalSubjects)
                .totalDocuments(totalDocuments)
                .studentsPerClass(studentsPerClass)
                .studentsPerStream(studentsPerStream)
                .build();
    }
}
