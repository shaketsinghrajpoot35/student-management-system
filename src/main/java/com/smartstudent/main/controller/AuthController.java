package com.smartstudent.main.controller;

import com.smartstudent.main.dto.request.LoginRequestDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.AuthResponseDTO;
import com.smartstudent.main.service.AuthService;
import com.smartstudent.main.dto.request.ForgotPasswordRequestDTO;
import com.smartstudent.main.dto.request.VerifyOtpRequestDTO;
import com.smartstudent.main.dto.request.ResetPasswordRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartstudent.main.util.SecurityUtil;
import com.smartstudent.main.entity.Admin;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final SecurityUtil securityUtil;

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        log.info("Login request received for user: {}", request.getUsername());
        return ResponseEntity.ok(authService.authenticateAdmin(request));
    }

    /**
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> register(
            @Valid @RequestBody com.smartstudent.main.dto.request.RegisterRequestDTO request) {
        log.info("Register request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        log.info("Forgot password requested for: {}", request.getEmail());
        authService.generateAndSendOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponseDTO.success("OTP sent to your email", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        log.info("Verifying OTP for: {}", request.getEmail());
        authService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponseDTO.success("OTP verified successfully", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        log.info("Resetting password for: {}", request.getEmail());
        authService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponseDTO.success("Password reset successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> getMe() {
        Admin admin = securityUtil.getCurrentAdmin();
        AuthResponseDTO response = AuthResponseDTO.builder()
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .schoolName(admin.getSchoolName())
                .role(admin.getRole())
                .schoolCode(admin.getSchoolCode())
                .isApproved(admin.isApproved())
                .build();
        return ResponseEntity.ok(ApiResponseDTO.success("Profile retrieved", response));
    }
}
