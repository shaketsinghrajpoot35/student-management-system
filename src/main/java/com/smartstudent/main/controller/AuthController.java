package com.smartstudent.main.controller;

import com.smartstudent.main.dto.request.LoginRequestDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.AuthResponseDTO;
import com.smartstudent.main.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        log.info("Login request received for user: {}", request.getUsername());
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(ApiResponseDTO.success("Login successful", response));
    }

    /**
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<Void>> register(
            @Valid @RequestBody com.smartstudent.main.dto.request.RegisterRequestDTO request) {
        log.info("Register request received for email: {}", request.getEmail());
        authService.registerAdmin(request);
        return ResponseEntity.ok(ApiResponseDTO.success("Admin registered successfully", null));
    }
}
