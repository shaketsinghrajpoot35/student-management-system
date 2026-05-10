package com.smartstudent.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartstudent.main.dto.request.StudentRegistrationRequestDTO;
import com.smartstudent.main.dto.request.StudentUpdateRequestDTO;
import com.smartstudent.main.dto.response.*;
import com.smartstudent.main.enums.Stream;
import com.smartstudent.main.exception.BadRequestException;
import com.smartstudent.main.service.StudentService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    private <T> void validate(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Validation failed: " + errors);
        }
    }

    /**
     * POST /api/students/register
     * Accepts multipart/form-data: 'data' (JSON) + optional 'files' (document files)
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> registerStudent(
            @RequestPart("data") String studentDataJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {

        StudentRegistrationRequestDTO request =
                objectMapper.readValue(studentDataJson, StudentRegistrationRequestDTO.class);
        validate(request);
        log.info("Master registration request for student: {}",
                request.getPersonalInfo().getSamagraId());

        StudentResponseDTO response = studentService.registerStudent(request, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Student registered successfully", response));
    }

    /**
     * GET /api/students?name=&samagraId=&className=&rollNumber=&admissionNumber=&stream=
     *                   &page=0&size=10&sortBy=fullName&sortDir=asc
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<StudentResponseDTO>>> searchStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String samagraId,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String rollNumber,
            @RequestParam(required = false) String admissionNumber,
            @RequestParam(required = false) Stream stream,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PagedResponseDTO<StudentResponseDTO> result = studentService.searchStudents(
                name, samagraId, className, rollNumber, admissionNumber, stream,
                page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponseDTO.success("Students retrieved", result));
    }

    /**
     * GET /api/students/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDTO.success("Student retrieved", studentService.getStudentById(id)));
    }

    /**
     * GET /api/students/{id}/full-details
     */
    @GetMapping("/{id}/full-details")
    public ResponseEntity<ApiResponseDTO<StudentFullDetailsResponseDTO>> getFullDetails(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDTO.success("Full student details retrieved",
                        studentService.getFullStudentDetails(id)));
    }

    /**
     * PUT /api/students/{id}
     * Accepts multipart/form-data: 'data' (JSON) + optional 'files'
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @RequestPart("data") String studentDataJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {

        StudentUpdateRequestDTO request =
                objectMapper.readValue(studentDataJson, StudentUpdateRequestDTO.class);
        validate(request);
        log.info("Update request for student ID: {}", id);
        StudentResponseDTO response = studentService.updateStudent(id, request, files);
        return ResponseEntity.ok(ApiResponseDTO.success("Student updated successfully", response));
    }

    /**
     * DELETE /api/students/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteStudent(@PathVariable Long id) {
        log.info("Delete request for student ID: {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Student deleted successfully"));
    }
}
