package com.smartstudent.main.controller;

import com.smartstudent.main.dto.request.AttendanceRequestDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.AttendanceResponseDTO;
import com.smartstudent.main.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    public ResponseEntity<ApiResponseDTO<Void>> markAttendance(@RequestBody AttendanceRequestDTO request) {
        attendanceService.markAttendance(request);
        return ResponseEntity.ok(ApiResponseDTO.success("Attendance marked successfully"));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponseDTO<List<AttendanceResponseDTO>>> getStudentAttendance(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponseDTO.success("Attendance retrieved", attendanceService.getAttendanceByStudent(studentId)));
    }

    @GetMapping("/class")
    public ResponseEntity<ApiResponseDTO<List<AttendanceResponseDTO>>> getClassAttendance(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate searchDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(ApiResponseDTO.success("Class attendance retrieved", 
                attendanceService.getAttendanceByClassAndDate(className, searchDate)));
    }
}
