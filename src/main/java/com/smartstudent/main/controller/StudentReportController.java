package com.smartstudent.main.controller;

import com.smartstudent.main.service.StudentExportService;
import com.smartstudent.main.service.StudentPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class StudentReportController {

    private final StudentPdfService studentPdfService;
    private final StudentExportService studentExportService;

    @GetMapping("/student/{id}")
    public ResponseEntity<InputStreamResource> downloadStudentRegistrationForm(@PathVariable Long id) {
        ByteArrayInputStream bis = studentPdfService.generateStudentRegistrationForm(id);
        
        String fileName = "student_registration_" + id + ".pdf";
        return ResponseEntity
                .ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<InputStreamResource> exportToCsv(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String samagraId,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String admissionNumber,
            @RequestParam(required = false) String stream) {
        
        ByteArrayInputStream bis = studentExportService.exportStudentsToCsv(name, samagraId, className, admissionNumber, stream);

        return ResponseEntity
                .ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(bis));
    }
}
