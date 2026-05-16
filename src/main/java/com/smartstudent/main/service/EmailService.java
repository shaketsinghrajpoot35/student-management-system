package com.smartstudent.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); 
            message.setTo(toEmail);
            message.setSubject("EduTrack - Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp + "\n\nThis OTP is valid for 10 minutes.\nIf you didn't request this, please ignore this email.");

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            log.warn("=========================================================");
            log.warn("DEVELOPMENT MODE: Your OTP is: {}", otp);
            log.warn("=========================================================");
        }
    }
}
