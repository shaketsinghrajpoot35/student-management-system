package com.smartstudent.main.service;

import com.smartstudent.main.dto.request.AcademicInfoDTO;
import com.smartstudent.main.dto.response.AcademicDetailsResponseDTO;

public interface AcademicDetailsService {
    AcademicDetailsResponseDTO getByStudentId(Long studentId);
    AcademicDetailsResponseDTO update(Long id, AcademicInfoDTO dto);
}
