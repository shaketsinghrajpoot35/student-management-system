package com.smartstudent.main.mapper;

import com.smartstudent.main.dto.request.AcademicInfoDTO;
import com.smartstudent.main.dto.response.AcademicDetailsResponseDTO;
import com.smartstudent.main.entity.AcademicDetails;
import org.springframework.stereotype.Component;

@Component
public class AcademicDetailsMapper {

    public AcademicDetails toEntity(AcademicInfoDTO dto) {
        if (dto == null) return null;
        return AcademicDetails.builder()
                .className(dto.getClassName())
                .section(dto.getSection())
                .rollNumber(dto.getRollNumber())
                .admissionNumber(dto.getAdmissionNumber())
                .board(dto.getBoard())
                .academicYear(dto.getAcademicYear())
                .previousSchool(dto.getPreviousSchool())
                .previousPercentage(dto.getPreviousPercentage())
                .stream(dto.getStream())
                .build();
    }

    public void updateFromDTO(AcademicInfoDTO dto, AcademicDetails entity) {
        if (dto == null || entity == null) return;
        if (dto.getClassName() != null) entity.setClassName(dto.getClassName());
        if (dto.getSection() != null) entity.setSection(dto.getSection());
        if (dto.getRollNumber() != null) entity.setRollNumber(dto.getRollNumber());
        if (dto.getAdmissionNumber() != null) entity.setAdmissionNumber(dto.getAdmissionNumber());
        if (dto.getBoard() != null) entity.setBoard(dto.getBoard());
        if (dto.getAcademicYear() != null) entity.setAcademicYear(dto.getAcademicYear());
        if (dto.getPreviousSchool() != null) entity.setPreviousSchool(dto.getPreviousSchool());
        if (dto.getPreviousPercentage() != null) entity.setPreviousPercentage(dto.getPreviousPercentage());
        if (dto.getStream() != null) entity.setStream(dto.getStream());
    }

    public AcademicDetailsResponseDTO toResponseDTO(AcademicDetails entity) {
        if (entity == null) return null;
        return AcademicDetailsResponseDTO.builder()
                .id(entity.getId())
                .className(entity.getClassName())
                .section(entity.getSection())
                .rollNumber(entity.getRollNumber())
                .admissionNumber(entity.getAdmissionNumber())
                .board(entity.getBoard())
                .academicYear(entity.getAcademicYear())
                .previousSchool(entity.getPreviousSchool())
                .previousPercentage(entity.getPreviousPercentage())
                .stream(entity.getStream())
                .build();
    }
}
