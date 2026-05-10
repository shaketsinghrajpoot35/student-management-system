package com.smartstudent.main.controller;

import com.smartstudent.main.dto.request.AcademicInfoDTO;
import com.smartstudent.main.dto.response.AcademicDetailsResponseDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.service.AcademicDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
public class AcademicDetailsController {

    private final AcademicDetailsService academicDetailsService;

    /**
     * GET /api/academic/{studentId}
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponseDTO<AcademicDetailsResponseDTO>> getByStudentId(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Academic details retrieved",
                academicDetailsService.getByStudentId(studentId)));
    }

    /**
     * PUT /api/academic/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AcademicDetailsResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicInfoDTO dto) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Academic details updated",
                academicDetailsService.update(id, dto)));
    }
}
