package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.LoginRequestDTO;
import com.smartstudent.main.dto.response.AuthResponseDTO;

import com.smartstudent.main.dto.request.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO request);
    void registerAdmin(RegisterRequestDTO request);
    void generateAndSendOtp(String email);
    void verifyOtp(String email, String otp);
    void resetPassword(String email, String newPassword);
}
