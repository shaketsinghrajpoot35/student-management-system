package com.smartstudent.main.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String schoolName;

    private String role; // Optional: ROLE_ADMIN or ROLE_TEACHER

    private String schoolCode; // For Teachers to join an existing school
}
