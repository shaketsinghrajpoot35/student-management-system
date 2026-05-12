package com.smartstudent.main.repository;

import com.smartstudent.main.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    Optional<BankDetails> findByStudentId(Long studentId);
    boolean existsByAccountNumber(String accountNumber);
}
