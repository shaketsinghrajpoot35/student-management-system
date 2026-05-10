package com.smartstudent.main.mapper;

import com.smartstudent.main.dto.request.SubjectDTO;
import com.smartstudent.main.dto.response.SubjectResponseDTO;
import com.smartstudent.main.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public Subject toEntity(SubjectDTO dto) {
        if (dto == null) return null;
        return Subject.builder()
                .subjectName(dto.getSubjectName())
                .subjectCode(dto.getSubjectCode())
                .description(dto.getDescription())
                .build();
    }

    public SubjectResponseDTO toResponseDTO(Subject entity) {
        if (entity == null) return null;
        return SubjectResponseDTO.builder()
                .id(entity.getId())
                .subjectName(entity.getSubjectName())
                .subjectCode(entity.getSubjectCode())
                .description(entity.getDescription())
                .build();
    }
}
