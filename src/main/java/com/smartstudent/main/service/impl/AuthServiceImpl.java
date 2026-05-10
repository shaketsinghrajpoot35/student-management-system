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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        String token = jwtUtil.generateToken(authentication.getName());

        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow();

        log.info("Login successful for user: {}", request.getUsername());

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .role(admin.getRole())
                .expiresIn(jwtExpiration)
                .build();
    }
}
