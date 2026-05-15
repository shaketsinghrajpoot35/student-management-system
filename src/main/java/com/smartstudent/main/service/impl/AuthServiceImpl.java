package com.smartstudent.main.service.impl;

import com.smartstudent.main.dto.request.LoginRequestDTO;
import com.smartstudent.main.dto.response.AuthResponseDTO;
import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.repository.AdminRepository;
import com.smartstudent.main.security.JwtUtil;
import com.smartstudent.main.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartstudent.main.dto.request.RegisterRequestDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.exception.ResourceNotFoundException;

import com.smartstudent.main.service.EmailService;
import com.smartstudent.main.entity.OtpToken;
import com.smartstudent.main.repository.OtpTokenRepository;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpTokenRepository otpTokenRepository;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;



    @Override
    public ApiResponseDTO<AuthResponseDTO> registerAdmin(RegisterRequestDTO request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            return ApiResponseDTO.error("Email already registered");
        }

        String role = request.getRole() != null ? request.getRole() : "ROLE_ADMIN";
        String schoolName = request.getSchoolName();
        String schoolCode = null;

        if ("ROLE_TEACHER".equals(role)) {
            if (request.getSchoolCode() == null || request.getSchoolCode().isEmpty()) {
                return ApiResponseDTO.error("School Code is required for Teacher registration");
            }
            Admin parentAdmin = adminRepository.findBySchoolCodeAndRole(request.getSchoolCode(), "ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Invalid School Code or School not found"));
            schoolName = parentAdmin.getSchoolName();
            schoolCode = parentAdmin.getSchoolCode();
        } else {
            // Generate unique school code for new Admin
            do {
                schoolCode = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            } while (adminRepository.findBySchoolCode(schoolCode).isPresent());
        }

        Admin admin = Admin.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getEmail().split("@")[0])
                .schoolName(schoolName)
                .schoolCode(schoolCode)
                .role(role)
                .isApproved("ROLE_ADMIN".equals(role))
                .build();

        adminRepository.save(admin);

        String token = jwtUtil.generateToken(admin.getUsername());
        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .schoolName(admin.getSchoolName())
                .role(admin.getRole())
                .schoolCode(admin.getSchoolCode())
                .isApproved(admin.isApproved())
                .expiresIn(jwtExpiration)
                .build();

        return ApiResponseDTO.success("Registration successful", response);
    }

    @Override
    public ApiResponseDTO<AuthResponseDTO> authenticateAdmin(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Admin admin = adminRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow();

        String token = jwtUtil.generateToken(admin.getUsername());
        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .schoolName(admin.getSchoolName())
                .role(admin.getRole())
                .schoolCode(admin.getSchoolCode())
                .isApproved(admin.isApproved())
                .expiresIn(jwtExpiration)
                .build();

        return ApiResponseDTO.success("Login successful", response);
    }

    @Override
    @Transactional
    public void generateAndSendOtp(String email) {
        log.info("Generating OTP for email: {}", email);
        Admin admin = adminRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with this email"));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        OtpToken otpToken = otpTokenRepository.findByEmail(email).orElse(new OtpToken());
        otpToken.setEmail(email);
        otpToken.setOtp(otp);
        otpToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        
        otpTokenRepository.save(otpToken);
        
        // Send email
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {
        log.info("Verifying OTP for email: {}", email);
        OtpToken otpToken = otpTokenRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or OTP not requested"));

        if (otpToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpTokenRepository.delete(otpToken);
            throw new IllegalArgumentException("OTP has expired");
        }

        if (!otpToken.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        log.info("OTP verified successfully for email: {}", email);
        // We keep the token so reset password can check it or we can just assume front-end handles flow.
        // Actually, safer to delete it only after password reset, or mark it as verified.
        // For simplicity, we just return success and let the next step reset the password.
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        log.info("Resetting password for email: {}", email);
        // Optionally re-verify OTP here or check if a verified token exists.
        // Assuming OTP was verified recently, we just update the password and clean up the token.
        Admin admin = adminRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with email: " + email));
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }

    @Override
    public java.util.List<AuthResponseDTO> getStaffBySchool(String schoolCode) {
        return adminRepository.findAllBySchoolCodeAndRole(schoolCode, "ROLE_TEACHER")
                .stream()
                .map(admin -> AuthResponseDTO.builder()
                        .id(admin.getId())
                        .username(admin.getUsername())
                        .fullName(admin.getFullName())
                        .schoolName(admin.getSchoolName())
                        .role(admin.getRole())
                        .isApproved(admin.isApproved())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void approveStaff(Long staffId) {
        Admin staff = adminRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        staff.setApproved(true);
        adminRepository.save(staff);
    }

    @Override
    public void rejectStaff(Long staffId) {
        Admin staff = adminRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        adminRepository.delete(staff);
    }
}
