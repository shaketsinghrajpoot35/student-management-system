package com.smartstudent.main.service.impl;

import com.smartstudent.main.dto.request.BankDetailsDTO;
import com.smartstudent.main.dto.response.BankDetailsResponseDTO;
import com.smartstudent.main.entity.BankDetails;
import com.smartstudent.main.exception.ResourceNotFoundException;
import com.smartstudent.main.mapper.BankDetailsMapper;
import com.smartstudent.main.repository.BankDetailsRepository;
import com.smartstudent.main.service.BankDetailsService;
import com.smartstudent.main.util.SecurityUtil;
import com.smartstudent.main.entity.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BankDetailsServiceImpl implements BankDetailsService {

    private final BankDetailsRepository repository;
    private final BankDetailsMapper mapper;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public BankDetailsResponseDTO getByStudentId(Long studentId) {
        Admin admin = securityUtil.getCurrentAdmin();
        BankDetails entity = repository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bank details", "studentId", studentId));
        if (entity.getStudent() != null && entity.getStudent().getAdmin() != null &&
            !entity.getStudent().getAdmin().getId().equals(admin.getId())) {
            throw new RuntimeException("Access denied");
        }
        return mapper.toResponseDTO(entity);
    }

    @Override
    public BankDetailsResponseDTO update(Long id, BankDetailsDTO dto) {
        Admin admin = securityUtil.getCurrentAdmin();
        log.info("Updating bank details ID: {}", id);
        BankDetails entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank details", "id", id));
        if (entity.getStudent() != null && entity.getStudent().getAdmin() != null &&
            !entity.getStudent().getAdmin().getId().equals(admin.getId())) {
            throw new RuntimeException("Access denied");
        }
        mapper.updateFromDTO(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }
}
