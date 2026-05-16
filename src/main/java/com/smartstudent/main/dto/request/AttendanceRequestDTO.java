package com.smartstudent.main.dto.request;

import com.smartstudent.main.enums.AttendanceStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceRequestDTO {
    private LocalDate date;
    private List<StudentAttendanceItem> attendanceList;

    @Data
    public static class StudentAttendanceItem {
        private Long studentId;
        private AttendanceStatus status;
        private String remarks;
    }
}
