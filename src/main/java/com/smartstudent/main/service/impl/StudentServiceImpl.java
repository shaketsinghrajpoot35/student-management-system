package com.smartstudent.main.service.impl;

import com.smartstudent.main.dto.request.*;
import com.smartstudent.main.dto.response.*;
import com.smartstudent.main.entity.*;
import com.smartstudent.main.enums.Stream;
import com.smartstudent.main.exception.DuplicateResourceException;
import com.smartstudent.main.exception.ResourceNotFoundException;
import com.smartstudent.main.mapper.*;
import com.smartstudent.main.repository.*;
import com.smartstudent.main.service.StudentService;
import com.smartstudent.main.util.FileStorageUtil;
import com.smartstudent.main.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AcademicDetailsRepository academicDetailsRepository;
    private final SubjectRepository subjectRepository;
    private final StudentDocumentRepository documentRepository;
    private final BankDetailsRepository bankDetailsRepository;

    private final StudentMapper studentMapper;
    private final AcademicDetailsMapper academicDetailsMapper;
    private final DocumentMapper documentMapper;
    private final BankDetailsMapper bankDetailsMapper;
    private final SubjectMapper subjectMapper;

    private final FileStorageUtil fileStorageUtil;
    private final SecurityUtil securityUtil;

    // ==========================================
    // REGISTER STUDENT (Master Registration)
    // ==========================================
    @Override
    public StudentResponseDTO registerStudent(StudentRegistrationRequestDTO request,
                                             List<MultipartFile> files) {
        Admin admin = securityUtil.getCurrentAdmin();
        log.info("Registering student with Samagra ID: {} for admin: {}", request.getPersonalInfo().getSamagraId(), admin.getUsername());

        String samagraIdHash = com.smartstudent.main.util.EncryptionUtil.hashForSearch(request.getPersonalInfo().getSamagraId());
        if (studentRepository.existsBySamagraIdHashAndAdmin(samagraIdHash, admin)) {
            throw new DuplicateResourceException(
                    "Student already exists with Samagra ID: " + request.getPersonalInfo().getSamagraId());
        }

        if (request.getAcademicInfo() == null || request.getAcademicInfo().getAdmissionNumber() == null || request.getAcademicInfo().getAdmissionNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Admission Number is required");
        }
        
        String admHash = com.smartstudent.main.util.EncryptionUtil.hashForSearch(request.getAcademicInfo().getAdmissionNumber().trim());
        if (academicDetailsRepository.existsByAdmNoHashAndStudentAdmin(admHash, admin)) {
            throw new DuplicateResourceException(
                    "Admission Number already exists for your account: " + request.getAcademicInfo().getAdmissionNumber());
        }

        // Map & save student
        Student student = studentMapper.toEntity(request.getPersonalInfo());
        student.setAdmin(admin);
        student = studentRepository.save(student);

        // Academic details
        if (request.getAcademicInfo() != null) {
            AcademicDetails academicDetails = academicDetailsMapper.toEntity(request.getAcademicInfo());
            academicDetails.setStudent(student);
            academicDetailsRepository.save(academicDetails);
        }

        // Bank details
        if (request.getBankDetails() != null) {
            BankDetails bankDetails = bankDetailsMapper.toEntity(request.getBankDetails());
            bankDetails.setStudent(student);
            bankDetailsRepository.save(bankDetails);
        }

        // Subjects
        if (!CollectionUtils.isEmpty(request.getSubjects())) {
            List<Subject> subjects = resolveSubjects(request.getSubjects(), admin);
            student.setSubjects(subjects);
            studentRepository.save(student);
        }

        // Document metadata (without files)
        if (!CollectionUtils.isEmpty(request.getDocuments())) {
            List<StudentDocument> docs = new ArrayList<>();
            for (int i = 0; i < request.getDocuments().size(); i++) {
                DocumentDTO docDto = request.getDocuments().get(i);
                StudentDocument doc = documentMapper.toEntity(docDto);
                doc.setStudent(student);

                // Attach uploaded file if available
                if (files != null && i < files.size() && !files.get(i).isEmpty()) {
                    MultipartFile file = files.get(i);
                    try {
                        fileStorageUtil.validateFile(file);
                        byte[] encryptedData = fileStorageUtil.encryptToByteArray(file.getBytes());
                        doc.setFileName(file.getOriginalFilename());
                        doc.setFileData(encryptedData);
                    } catch (Exception e) {
                        log.error("Failed to process file upload for student {}", student.getId(), e);
                        throw new RuntimeException("Could not store file", e);
                    }
                }
                docs.add(doc);
            }
            documentRepository.saveAll(docs);
        }

        log.info("Student registered successfully with ID: {}", student.getId());
        return studentMapper.toResponseDTO(student);
    }

    // ==========================================
    // GET STUDENT BY ID
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentById(id);
        return studentMapper.toResponseDTO(student);
    }

    // ==========================================
    // GET FULL DETAILS
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    public StudentFullDetailsResponseDTO getFullStudentDetails(Long id) {
        Student student = findStudentById(id);

        AcademicDetails academic = academicDetailsRepository.findByStudentId(id).orElse(null);
        BankDetails bank = bankDetailsRepository.findByStudentId(id).orElse(null);
        List<StudentDocument> docs = documentRepository.findByStudentId(id);

        List<SubjectResponseDTO> subjectDTOs = student.getSubjects().stream()
                .map(subjectMapper::toResponseDTO)
                .collect(Collectors.toList());

        List<DocumentResponseDTO> docDTOs = docs.stream()
                .map(documentMapper::toResponseDTO)
                .collect(Collectors.toList());

        return StudentFullDetailsResponseDTO.builder()
                .id(id)
                .personalInfo(studentMapper.toResponseDTO(student))
                .academicDetails(academicDetailsMapper.toResponseDTO(academic))
                .subjects(subjectDTOs)
                .documents(docDTOs)
                .bankDetails(bankDetailsMapper.toResponseDTO(bank))
                .build();
    }

    // ==========================================
    // UPDATE STUDENT
    // ==========================================
    @Override
    public StudentResponseDTO updateStudent(Long id, StudentUpdateRequestDTO request,
                                           List<MultipartFile> files) {
        log.info("Updating student with ID: {}", id);
        Student student = findStudentById(id);

        // Update personal info
        if (request.getPersonalInfo() != null) {
            String newSamagraId = request.getPersonalInfo().getSamagraId();
            if (newSamagraId != null && !newSamagraId.equals(student.getSamagraId())) {
                Admin admin = securityUtil.getCurrentAdmin();
                String samagraIdHash = com.smartstudent.main.util.EncryptionUtil.hashForSearch(newSamagraId);
                if (studentRepository.existsBySamagraIdHashAndAdmin(samagraIdHash, admin)) {
                    throw new DuplicateResourceException("Another student already exists with Samagra ID: " + newSamagraId);
                }
            }
            studentMapper.updateEntityFromDTO(request.getPersonalInfo(), student);
            student = studentRepository.save(student);
        }

        // Update academic details
        if (request.getAcademicInfo() != null) {
            AcademicDetails academic = academicDetailsRepository.findByStudentId(id)
                    .orElse(new AcademicDetails());
            academic.setStudent(student);
            
            String newAdmNo = request.getAcademicInfo().getAdmissionNumber();
            if (newAdmNo == null || newAdmNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Admission Number cannot be empty");
            }
            
            if (!newAdmNo.equals(academic.getAdmissionNumber())) {
                Admin admin = securityUtil.getCurrentAdmin();
                String admHash = com.smartstudent.main.util.EncryptionUtil.hashForSearch(newAdmNo.trim());
                if (academicDetailsRepository.existsByAdmNoHashAndStudentAdmin(admHash, admin)) {
                    throw new DuplicateResourceException("Admission Number already exists for your account: " + newAdmNo);
                }
            }

            academicDetailsMapper.updateFromDTO(request.getAcademicInfo(), academic);
            academicDetailsRepository.save(academic);
        }

        // Update bank details
        if (request.getBankDetails() != null) {
            BankDetails bank = bankDetailsRepository.findByStudentId(id)
                    .orElse(new BankDetails());
            bank.setStudent(student);
            bankDetailsMapper.updateFromDTO(request.getBankDetails(), bank);
            bankDetailsRepository.save(bank);
        }

        // Update subjects (replace all)
        if (!CollectionUtils.isEmpty(request.getSubjects())) {
            Admin admin = securityUtil.getCurrentAdmin();
            List<Subject> subjects = resolveSubjects(request.getSubjects(), admin);
            student.setSubjects(subjects);
            student = studentRepository.save(student);
        }

        // Update documents (new ones only)
        if (!CollectionUtils.isEmpty(request.getDocuments())) {
            for (int i = 0; i < request.getDocuments().size(); i++) {
                DocumentDTO docDto = request.getDocuments().get(i);
                StudentDocument doc = documentMapper.toEntity(docDto);
                doc.setStudent(student);
                if (files != null && i < files.size() && !files.get(i).isEmpty()) {
                    MultipartFile file = files.get(i);
                    try {
                        fileStorageUtil.validateFile(file);
                        byte[] encryptedData = fileStorageUtil.encryptToByteArray(file.getBytes());
                        doc.setFileName(file.getOriginalFilename());
                        doc.setFileData(encryptedData);
                    } catch (Exception e) {
                        log.error("Failed to process file update for student {}", student.getId(), e);
                        throw new RuntimeException("Could not store file", e);
                    }
                }
                documentRepository.save(doc);
            }
        }

        log.info("Student updated successfully: {}", id);
        return studentMapper.toResponseDTO(student);
    }

    // ==========================================
    // DELETE STUDENT
    // ==========================================
    @Override
    public void deleteStudent(Long id) {
        log.info("Deleting student with ID: {}", id);
        Student student = findStudentById(id);

        // StudentDocuments will be deleted by CascadeType.ALL if configured, 
        // or we can let JPA handle it. The database BLOBs are deleted with the entity.

        studentRepository.delete(student);
        log.info("Student deleted: {}", id);
    }

    // ==========================================
    // SEARCH STUDENTS
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<StudentResponseDTO> searchStudents(
            String name, String samagraId, String className,
            String rollNumber, String admissionNumber, Stream stream,
            int page, int size, String sortBy, String sortDir) {

        Admin admin = securityUtil.getCurrentAdmin();
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> studentPage = studentRepository.searchStudents(
                admin, name, samagraId, className, rollNumber, admissionNumber, stream, pageable);

        List<StudentResponseDTO> content = studentPage.getContent()
                .stream()
                .map(studentMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<StudentResponseDTO>builder()
                .content(content)
                .pageNumber(studentPage.getNumber())
                .pageSize(studentPage.getSize())
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .last(studentPage.isLast())
                .build();
    }

    // ==========================================
    // HELPERS
    // ==========================================
    private Student findStudentById(Long id) {
        Admin admin = securityUtil.getCurrentAdmin();
        return studentRepository.findByIdAndAdmin(id, admin)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    private List<Subject> resolveSubjects(List<SubjectDTO> subjectDTOs, Admin admin) {
        return subjectDTOs.stream().map(dto -> {
            String code = (dto.getSubjectCode() != null && !dto.getSubjectCode().isBlank())
                    ? dto.getSubjectCode() : null;
            
            if (code != null) {
                return subjectRepository.findBySubjectCodeAndAdmin(code, admin)
                        .orElseGet(() -> {
                            Subject s = subjectMapper.toEntity(dto);
                            s.setSubjectCode(code); // Ensure normalized
                            s.setAdmin(admin);
                            return subjectRepository.save(s);
                        });
            }
            return subjectRepository.findBySubjectNameAndAdmin(dto.getSubjectName(), admin)
                    .orElseGet(() -> {
                            Subject s = subjectMapper.toEntity(dto);
                            s.setSubjectCode(null); // Ensure null, not empty string
                            s.setAdmin(admin);
                            return subjectRepository.save(s);
                    });
        }).collect(Collectors.toList());
    }
}
