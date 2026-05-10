package com.smartstudent.main.controller;

import com.smartstudent.main.dto.request.BankDetailsDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.BankDetailsResponseDTO;
import com.smartstudent.main.service.BankDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-details")
@RequiredArgsConstructor
public class BankDetailsController {

    private final BankDetailsService bankDetailsService;

    /**
     * GET /api/bank-details/{studentId}
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponseDTO<BankDetailsResponseDTO>> getByStudentId(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Bank details retrieved",
                bankDetailsService.getByStudentId(studentId)));
    }

    /**
     * PUT /api/bank-details/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<BankDetailsResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody BankDetailsDTO dto) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Bank details updated",
                bankDetailsService.update(id, dto)));
    }
}
