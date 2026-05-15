package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.LoginRequestDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.AuthResponseDTO;
import com.smartstudent.main.dto.request.RegisterRequestDTO;

public interface AuthService {
    ApiResponseDTO<AuthResponseDTO> authenticateAdmin(LoginRequestDTO request);
    ApiResponseDTO<AuthResponseDTO> registerAdmin(RegisterRequestDTO request);
    void generateAndSendOtp(String email);
    void verifyOtp(String email, String otp);
    void resetPassword(String email, String newPassword);
    
    // Staff Management
    java.util.List<AuthResponseDTO> getStaffBySchool(String schoolCode);
    void approveStaff(Long staffId);
    void rejectStaff(Long staffId);
}
