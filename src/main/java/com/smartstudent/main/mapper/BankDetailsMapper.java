package com.smartstudent.main.mapper;

import com.smartstudent.main.dto.request.BankDetailsDTO;
import com.smartstudent.main.dto.response.BankDetailsResponseDTO;
import com.smartstudent.main.entity.BankDetails;
import org.springframework.stereotype.Component;

@Component
public class BankDetailsMapper {

    public BankDetails toEntity(BankDetailsDTO dto) {
        if (dto == null) return null;
        return BankDetails.builder()
                .bankName(dto.getBankName())
                .branchName(dto.getBranchName())
                .ifscCode(dto.getIfscCode())
                .accountNumber(dto.getAccountNumber())
                .accountHolderName(dto.getAccountHolderName())
                .build();
    }

    public void updateFromDTO(BankDetailsDTO dto, BankDetails entity) {
        if (dto == null || entity == null) return;
        if (dto.getBankName() != null) entity.setBankName(dto.getBankName());
        if (dto.getBranchName() != null) entity.setBranchName(dto.getBranchName());
        if (dto.getIfscCode() != null) entity.setIfscCode(dto.getIfscCode());
        if (dto.getAccountNumber() != null) entity.setAccountNumber(dto.getAccountNumber());
        if (dto.getAccountHolderName() != null) entity.setAccountHolderName(dto.getAccountHolderName());
    }

    public BankDetailsResponseDTO toResponseDTO(BankDetails entity) {
        if (entity == null) return null;
        return BankDetailsResponseDTO.builder()
                .id(entity.getId())
                .bankName(entity.getBankName())
                .branchName(entity.getBranchName())
                .ifscCode(entity.getIfscCode())
                .accountNumber(entity.getAccountNumber())
                .accountHolderName(entity.getAccountHolderName())
                .passbookFilePath(entity.getPassbookFilePath())
                .build();
    }
}
