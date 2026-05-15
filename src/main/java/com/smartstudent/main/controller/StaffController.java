package com.smartstudent.main.controller;

import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.AuthResponseDTO;
import com.smartstudent.main.util.SecurityUtil;
import com.smartstudent.main.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Slf4j
public class StaffController {

    private final AuthService authService;
    private final SecurityUtil securityUtil;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<AuthResponseDTO>>> getStaff() {
        String schoolCode = securityUtil.getCurrentAdmin().getSchoolCode();
        return ResponseEntity.ok(ApiResponseDTO.success("Staff list retrieved", authService.getStaffBySchool(schoolCode)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> approveStaff(@PathVariable Long id) {
        authService.approveStaff(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Staff approved successfully", null));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> rejectStaff(@PathVariable Long id) {
        authService.rejectStaff(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Staff removed successfully", null));
    }
}
