package com.smartstudent.main.repository;

import com.smartstudent.main.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Admin> findBySchoolCode(String schoolCode);
    Optional<Admin> findBySchoolCodeAndRole(String schoolCode, String role);
    java.util.List<Admin> findAllBySchoolCodeAndRole(String schoolCode, String role);
}
