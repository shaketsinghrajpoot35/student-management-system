package com.smartstudent.main.mapper;

import com.smartstudent.main.dto.request.PersonalInfoDTO;
import com.smartstudent.main.dto.response.StudentResponseDTO;
import com.smartstudent.main.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(PersonalInfoDTO dto) {
        if (dto == null) return null;
        return Student.builder()
                .samagraId(dto.getSamagraId())
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .bloodGroup(dto.getBloodGroup())
                .category(dto.getCategory())
                .religion(dto.getReligion())
                .nationality(dto.getNationality())
                .fatherName(dto.getFatherName())
                .motherName(dto.getMotherName())
                .guardianName(dto.getGuardianName())
                .mobileNumber(dto.getMobileNumber())
                .alternateMobileNumber(dto.getAlternateMobileNumber())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .admissionDate(dto.getAdmissionDate())
                .studentStatus(dto.getStudentStatus() != null ?
                        dto.getStudentStatus() : com.smartstudent.main.enums.StudentStatus.ACTIVE)
                .build();
    }

    public void updateEntityFromDTO(PersonalInfoDTO dto, Student student) {
        if (dto == null || student == null) return;
        if (dto.getSamagraId() != null) student.setSamagraId(dto.getSamagraId());
        if (dto.getFullName() != null) student.setFullName(dto.getFullName());
        if (dto.getGender() != null) student.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) student.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getBloodGroup() != null) student.setBloodGroup(dto.getBloodGroup());
        if (dto.getCategory() != null) student.setCategory(dto.getCategory());
        if (dto.getReligion() != null) student.setReligion(dto.getReligion());
        if (dto.getNationality() != null) student.setNationality(dto.getNationality());
        if (dto.getFatherName() != null) student.setFatherName(dto.getFatherName());
        if (dto.getMotherName() != null) student.setMotherName(dto.getMotherName());
        if (dto.getGuardianName() != null) student.setGuardianName(dto.getGuardianName());
        if (dto.getMobileNumber() != null) student.setMobileNumber(dto.getMobileNumber());
        if (dto.getAlternateMobileNumber() != null) student.setAlternateMobileNumber(dto.getAlternateMobileNumber());
        if (dto.getEmail() != null) student.setEmail(dto.getEmail());
        if (dto.getAddress() != null) student.setAddress(dto.getAddress());
        if (dto.getCity() != null) student.setCity(dto.getCity());
        if (dto.getState() != null) student.setState(dto.getState());
        if (dto.getPincode() != null) student.setPincode(dto.getPincode());
        if (dto.getAdmissionDate() != null) student.setAdmissionDate(dto.getAdmissionDate());
        if (dto.getStudentStatus() != null) student.setStudentStatus(dto.getStudentStatus());
    }

    public StudentResponseDTO toResponseDTO(Student student) {
        String admNo = (student.getAcademicDetails() != null) ? student.getAcademicDetails().getAdmissionNumber() : null;
        return StudentResponseDTO.builder()
                .id(student.getId())
                .samagraId(student.getSamagraId())
                .fullName(student.getFullName())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth())
                .bloodGroup(student.getBloodGroup())
                .category(student.getCategory())
                .religion(student.getReligion())
                .nationality(student.getNationality())
                .fatherName(student.getFatherName())
                .motherName(student.getMotherName())
                .guardianName(student.getGuardianName())
                .mobileNumber(student.getMobileNumber())
                .alternateMobileNumber(student.getAlternateMobileNumber())
                .email(student.getEmail())
                .address(student.getAddress())
                .city(student.getCity())
                .state(student.getState())
                .pincode(student.getPincode())
                .admissionDate(student.getAdmissionDate())
                .studentStatus(student.getStudentStatus())
                .admissionNumber(admNo)
                .photoPath(student.getPhotoPath())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }
}
