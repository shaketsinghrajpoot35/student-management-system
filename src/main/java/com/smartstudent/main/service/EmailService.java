package com.smartstudent.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@edutrack.com"); // Usually overridden by the actual authenticated SMTP account
            message.setTo(toEmail);
            message.setSubject("EduTrack - Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp + "\n\nThis OTP is valid for 10 minutes.\nIf you didn't request this, please ignore this email.");

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.warn("=========================================================");
            log.warn("Failed to send OTP email to {} due to mail configuration.", toEmail);
            log.warn("DEVELOPMENT MODE: Your OTP is: {}", otp);
            log.warn("=========================================================");
            // We DO NOT throw an exception here. 
            // If we throw, the @Transactional method rolls back and the OTP token is never saved.
            // This allows local testing without real SMTP credentials.
        }
    }
}
