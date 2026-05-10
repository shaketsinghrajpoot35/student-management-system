package com.smartstudent.main.security;

import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Admin not found with username: {}", username);
                    return new UsernameNotFoundException("Admin not found: " + username);
                });

        return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPassword(),
                List.of(new SimpleGrantedAuthority(admin.getRole()))
        );
    }
}
