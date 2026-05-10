package com.smartstudent.main.service.impl;

import com.smartstudent.main.dto.request.AcademicInfoDTO;
import com.smartstudent.main.dto.response.AcademicDetailsResponseDTO;
import com.smartstudent.main.entity.AcademicDetails;
import com.smartstudent.main.exception.ResourceNotFoundException;
import com.smartstudent.main.mapper.AcademicDetailsMapper;
import com.smartstudent.main.repository.AcademicDetailsRepository;
import com.smartstudent.main.service.AcademicDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AcademicDetailsServiceImpl implements AcademicDetailsService {

    private final AcademicDetailsRepository repository;
    private final AcademicDetailsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public AcademicDetailsResponseDTO getByStudentId(Long studentId) {
        AcademicDetails entity = repository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic details", "studentId", studentId));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public AcademicDetailsResponseDTO update(Long id, AcademicInfoDTO dto) {
        log.info("Updating academic details ID: {}", id);
        AcademicDetails entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic details", "id", id));
        mapper.updateFromDTO(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }
}
