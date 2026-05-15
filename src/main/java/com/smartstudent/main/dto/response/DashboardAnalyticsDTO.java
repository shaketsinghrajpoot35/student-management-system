package com.smartstudent.main.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardAnalyticsDTO {
    private long totalStudents;
    private long activeStudents;
    private long totalSubjects;
    private long totalDocuments;
    
    // Chart Data
    private Map<String, Long> studentsPerClass;
    private Map<String, Long> studentsPerStream;
}
