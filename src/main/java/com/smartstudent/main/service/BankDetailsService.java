package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.BankDetailsDTO;
import com.smartstudent.main.dto.response.BankDetailsResponseDTO;

public interface BankDetailsService {
    BankDetailsResponseDTO getByStudentId(Long studentId);
    BankDetailsResponseDTO update(Long id, BankDetailsDTO dto);
}
