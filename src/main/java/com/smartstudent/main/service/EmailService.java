package com.smartstudent.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Value("${app.mail.from:${spring.mail.username}}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String apiKey;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            Map<String, Object> sender = new HashMap<>();
            sender.put("email", fromEmail);
            sender.put("name", "EduTrack");

            Map<String, Object> to = new HashMap<>();
            to.put("email", toEmail);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", sender);
            body.put("to", List.of(to));
            body.put("subject", "EduTrack - Password Reset OTP");
            body.put("textContent", "Your OTP for password reset is: " + otp + "\n\nThis OTP is valid for 10 minutes.\nIf you didn't request this, please ignore this email.");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("OTP email sent successfully via HTTP API to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email via HTTP API to {}: {}", toEmail, e.getMessage());
            log.warn("=========================================================");
            log.warn("DEVELOPMENT MODE: Your OTP is: {}", otp);
            log.warn("=========================================================");
        }
    }
}
