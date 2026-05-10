package com.smartstudent.main.controller;

import com.smartstudent.main.dto.request.SubjectDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.SubjectResponseDTO;
import com.smartstudent.main.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * POST /api/subjects
     */
    @PostMapping("/api/subjects")
    public ResponseEntity<ApiResponseDTO<SubjectResponseDTO>> createSubject(
            @Valid @RequestBody SubjectDTO dto) {
        SubjectResponseDTO response = subjectService.createSubject(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Subject created", response));
    }

    /**
     * GET /api/subjects
     */
    @GetMapping("/api/subjects")
    public ResponseEntity<ApiResponseDTO<List<SubjectResponseDTO>>> getAllSubjects() {
        return ResponseEntity.ok(
                ApiResponseDTO.success("Subjects retrieved", subjectService.getAllSubjects()));
    }

    /**
     * PUT /api/subjects/{id}
     */
    @PutMapping("/api/subjects/{id}")
    public ResponseEntity<ApiResponseDTO<SubjectResponseDTO>> updateSubject(
            @PathVariable Long id,
            @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(
                ApiResponseDTO.success("Subject updated", subjectService.updateSubject(id, dto)));
    }

    /**
     * DELETE /api/subjects/{id}
     */
    @DeleteMapping("/api/subjects/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Subject deleted"));
    }

    /**
     * POST /api/students/{id}/subjects — assign subjects to a student
     */
    @PostMapping("/api/students/{id}/subjects")
    public ResponseEntity<ApiResponseDTO<List<SubjectResponseDTO>>> assignSubjects(
            @PathVariable Long id,
            @RequestBody List<@Valid SubjectDTO> subjects) {
        List<SubjectResponseDTO> response = subjectService.assignSubjectsToStudent(id, subjects);
        return ResponseEntity.ok(ApiResponseDTO.success("Subjects assigned to student", response));
    }

    /**
     * GET /api/students/{id}/subjects
     */
    @GetMapping("/api/students/{id}/subjects")
    public ResponseEntity<ApiResponseDTO<List<SubjectResponseDTO>>> getStudentSubjects(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDTO.success("Student subjects retrieved",
                        subjectService.getStudentSubjects(id)));
    }
}
