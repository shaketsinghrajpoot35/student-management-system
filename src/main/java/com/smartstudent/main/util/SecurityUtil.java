package com.smartstudent.main.util;

import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final AdminRepository adminRepository;

    public Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String usernameOrEmail = authentication.getName();
            return adminRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new RuntimeException("Current admin not found"));
        }
        throw new RuntimeException("No authenticated user found");
    }
}
