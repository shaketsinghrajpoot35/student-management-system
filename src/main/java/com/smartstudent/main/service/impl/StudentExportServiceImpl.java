package com.smartstudent.main.service.impl;

import com.smartstudent.main.entity.*;
import com.smartstudent.main.enums.Stream;
import com.smartstudent.main.repository.StudentRepository;
import com.smartstudent.main.service.StudentExportService;
import com.smartstudent.main.util.EncryptionUtil;
import com.smartstudent.main.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentExportServiceImpl implements StudentExportService {

    private final StudentRepository studentRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportStudentsToCsv(String name, String samagraId, String className, String admissionNumber, String streamStr) {
        Admin admin = securityUtil.getCurrentAdmin();
        // Normalize empty strings to null for correct database matching
        String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String searchClassName = (className != null && !className.trim().isEmpty()) ? className.trim() : null;
        Stream stream = (streamStr != null && !streamStr.trim().isEmpty()) ? Stream.valueOf(streamStr.trim()) : null;
        
        String samagraIdHash = (samagraId != null && !samagraId.trim().isEmpty()) ? EncryptionUtil.hashForSearch(samagraId.trim()) : null;
        String admissionNumberHash = (admissionNumber != null && !admissionNumber.trim().isEmpty()) ? EncryptionUtil.hashForSearch(admissionNumber.trim()) : null;

        // Fetch all matching students (max 5000 for safety)
        Page<Student> studentsPage = studentRepository.searchStudents(
                admin.getSchoolCode(), searchName, samagraIdHash, searchClassName, null, admissionNumberHash, stream, 
                PageRequest.of(0, 5000, Sort.by("fullName").ascending())
        );
        List<Student> students = studentsPage.getContent();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "ID", "Full Name", "Samagra ID", "Gender", "DOB", "Mobile", "Email", "Address", "City", "State", "Pincode",
                     "Class", "Section", "Roll No", "Admission No", "Board", "Academic Year", "Stream",
                     "Bank Name", "Branch", "IFSC", "Account No", "Holder Name",
                     "Subjects", "Documents"
             ))) {

            for (Student s : students) {
                AcademicDetails ad = s.getAcademicDetails();
                BankDetails bd = s.getBankDetails();
                
                String subjects = s.getSubjects().stream()
                        .map(Subject::getSubjectName)
                        .collect(Collectors.joining("; "));
                
                String documents = s.getDocuments().stream()
                        .map(d -> d.getDocumentType() + "(" + (d.getDocumentNumber() != null ? d.getDocumentNumber() : "N/A") + ")")
                        .collect(Collectors.joining("; "));

                csvPrinter.printRecord(
                        s.getId(),
                        s.getFullName(),
                        formatAsText(s.getSamagraId()),
                        s.getGender(),
                        s.getDateOfBirth(),
                        formatAsText(s.getMobileNumber()),
                        s.getEmail(),
                        s.getAddress(),
                        s.getCity(),
                        s.getState(),
                        s.getPincode(),
                        ad != null ? ad.getClassName() : "",
                        ad != null ? ad.getSection() : "",
                        ad != null ? ad.getRollNumber() : "",
                        ad != null ? formatAsText(ad.getAdmissionNumber()) : "",
                        ad != null ? ad.getBoard() : "",
                        ad != null ? ad.getAcademicYear() : "",
                        ad != null ? ad.getStream() : "",
                        bd != null ? bd.getBankName() : "",
                        bd != null ? bd.getBranchName() : "",
                        bd != null ? bd.getIfscCode() : "",
                        bd != null ? formatAsText(bd.getAccountNumber()) : "",
                        bd != null ? bd.getAccountHolderName() : "",
                        subjects,
                        documents
                );
            }

            csvPrinter.flush();
            writer.flush();
        } catch (IOException e) {
            log.error("Error creating CSV export: ", e);
            throw new RuntimeException("Failed to generate CSV export", e);
        }
        
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String formatAsText(String value) {
        if (value == null || value.trim().isEmpty()) return "";
        // Force Excel to treat as text using ="value" format
        return "=\"" + value + "\"";
    }
}
