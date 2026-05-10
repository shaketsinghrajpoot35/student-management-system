package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.LoginRequestDTO;
import com.smartstudent.main.dto.response.AuthResponseDTO;

import com.smartstudent.main.dto.request.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO request);
    void registerAdmin(RegisterRequestDTO request);
}
