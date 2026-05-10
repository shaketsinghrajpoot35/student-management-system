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

import com.smartstudent.main.dto.request.RegisterRequestDTO;
import com.smartstudent.main.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        String token = jwtUtil.generateToken(authentication.getName());

        Admin admin = adminRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        log.info("Login successful for user: {}", request.getUsername());

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .schoolName(admin.getSchoolName())
                .role(admin.getRole())
                .expiresIn(jwtExpiration)
                .build();
    }

    @Override
    public void registerAdmin(RegisterRequestDTO request) {
        log.info("Registering new admin with email: {}", request.getEmail());
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        
        Admin newAdmin = Admin.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName("Administrator")
                .schoolName(request.getSchoolName())
                .role("ROLE_ADMIN")
                .build();
        
        adminRepository.save(newAdmin);
        log.info("Admin registered successfully: {}", request.getEmail());
    }
}
