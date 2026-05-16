package com.smartstudent.main.dto.response;

import com.smartstudent.main.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AttendanceResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private LocalDate date;
    private AttendanceStatus status;
    private String remarks;
    private String markedByName;
}
