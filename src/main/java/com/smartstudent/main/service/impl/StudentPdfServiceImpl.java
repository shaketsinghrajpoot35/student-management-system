package com.smartstudent.main.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.smartstudent.main.dto.response.*;
import com.smartstudent.main.entity.Admin;
import com.smartstudent.main.service.StudentPdfService;
import com.smartstudent.main.service.StudentService;
import com.smartstudent.main.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentPdfServiceImpl implements StudentPdfService {

    private final StudentService studentService;
    private final SecurityUtil securityUtil;

    @Override
    public ByteArrayInputStream generateStudentRegistrationForm(Long studentId) {
        StudentFullDetailsResponseDTO details = studentService.getFullStudentDetails(studentId);
        Admin admin = securityUtil.getCurrentAdmin();
        String schoolName = admin.getSchoolName() != null ? admin.getSchoolName().toUpperCase() : "SMART STUDENT PORTAL";

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Font Styles
            Font schoolFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.BOLD, Color.BLUE.darker());
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.BOLD);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);

            // 1. School Name (Top Center)
            Paragraph schoolPara = new Paragraph(schoolName, schoolFont);
            schoolPara.setAlignment(Element.ALIGN_CENTER);
            document.add(schoolPara);

            // 2. Form Title
            Paragraph titlePara = new Paragraph("STUDENT REGISTRATION FORM", headerFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(10);
            document.add(titlePara);

            // Line Separator
            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell(new Phrase(" "));
            lineCell.setBorder(Rectangle.BOTTOM);
            lineCell.setBorderWidthBottom(2f);
            lineCell.setBorderColorBottom(Color.DARK_GRAY);
            lineTable.addCell(lineCell);
            document.add(lineTable);

            document.add(new Paragraph(" ")); // Spacer

            // --- 1. PERSONAL INFORMATION ---
            addStyledSectionTitle(document, "1. PERSONAL INFORMATION", sectionFont);
            StudentResponseDTO personal = details.getPersonalInfo();
            PdfPTable personalTable = new PdfPTable(4);
            personalTable.setWidthPercentage(100);
            personalTable.setWidths(new float[]{1.5f, 2f, 1.5f, 2f});
            personalTable.setSpacingBefore(10);
            personalTable.setSpacingAfter(15);

            addTableCell(personalTable, "Full Name:", personal.getFullName(), labelFont, normalFont);
            addTableCell(personalTable, "Samagra ID:", personal.getSamagraId(), labelFont, normalFont);
            addTableCell(personalTable, "Gender:", personal.getGender() != null ? personal.getGender().toString() : "", labelFont, normalFont);
            addTableCell(personalTable, "Date of Birth:", personal.getDateOfBirth() != null ? personal.getDateOfBirth().toString() : "", labelFont, normalFont);
            addTableCell(personalTable, "Mobile:", personal.getMobileNumber(), labelFont, normalFont);
            addTableCell(personalTable, "Email:", personal.getEmail(), labelFont, normalFont);
            addTableCell(personalTable, "Father:", personal.getFatherName(), labelFont, normalFont);
            addTableCell(personalTable, "Mother:", personal.getMotherName(), labelFont, normalFont);
            addTableCell(personalTable, "Address:", personal.getAddress(), labelFont, normalFont);
            addTableCell(personalTable, "City/State:", (personal.getCity() != null ? personal.getCity() : "") + " / " + (personal.getState() != null ? personal.getState() : ""), labelFont, normalFont);
            addTableCell(personalTable, "Pincode:", personal.getPincode(), labelFont, normalFont);
            addTableCell(personalTable, "Status:", personal.getStudentStatus() != null ? personal.getStudentStatus().toString() : "", labelFont, normalFont);
            document.add(personalTable);

            // --- 2. ACADEMIC DETAILS ---
            addStyledSectionTitle(document, "2. ACADEMIC DETAILS", sectionFont);
            AcademicDetailsResponseDTO academic = details.getAcademicDetails();
            if (academic != null) {
                PdfPTable academicTable = new PdfPTable(4);
                academicTable.setWidthPercentage(100);
                academicTable.setWidths(new float[]{1.5f, 2f, 1.5f, 2f});
                academicTable.setSpacingBefore(10);
                academicTable.setSpacingAfter(15);

                addTableCell(academicTable, "Class:", academic.getClassName(), labelFont, normalFont);
                addTableCell(academicTable, "Section:", academic.getSection(), labelFont, normalFont);
                addTableCell(academicTable, "Roll No:", academic.getRollNumber(), labelFont, normalFont);
                addTableCell(academicTable, "Adm No:", academic.getAdmissionNumber(), labelFont, normalFont);
                addTableCell(academicTable, "Stream:", academic.getStream() != null ? academic.getStream().toString() : "", labelFont, normalFont);
                addTableCell(academicTable, "Board:", academic.getBoard(), labelFont, normalFont);
                addTableCell(academicTable, "Year:", academic.getAcademicYear(), labelFont, normalFont);
                addTableCell(academicTable, "Prev. %:", academic.getPreviousPercentage() != null ? academic.getPreviousPercentage().toString() : "N/A", labelFont, normalFont);
                document.add(academicTable);
            }

            // --- 3. BANK DETAILS ---
            addStyledSectionTitle(document, "3. BANK DETAILS", sectionFont);
            BankDetailsResponseDTO bank = details.getBankDetails();
            if (bank != null) {
                PdfPTable bankTable = new PdfPTable(2);
                bankTable.setWidthPercentage(100);
                bankTable.setSpacingBefore(10);
                bankTable.setSpacingAfter(15);

                addTableCell(bankTable, "Bank Name:", bank.getBankName(), labelFont, normalFont);
                addTableCell(bankTable, "Branch Name:", bank.getBranchName(), labelFont, normalFont);
                addTableCell(bankTable, "IFSC Code:", bank.getIfscCode(), labelFont, normalFont);
                addTableCell(bankTable, "Account No:", bank.getAccountNumber(), labelFont, normalFont);
                addTableCell(bankTable, "Holder Name:", bank.getAccountHolderName(), labelFont, normalFont);
                document.add(bankTable);
            }

            // --- 4. DOCUMENTS INFORMATION ---
            addStyledSectionTitle(document, "4. DOCUMENT INFORMATION", sectionFont);
            if (details.getDocuments() != null && !details.getDocuments().isEmpty()) {
                PdfPTable docTable = new PdfPTable(3);
                docTable.setWidthPercentage(100);
                docTable.setWidths(new float[]{2f, 2f, 2f});
                docTable.setSpacingBefore(10);
                docTable.setSpacingAfter(15);

                // Table Header
                docTable.addCell(new PdfPCell(new Phrase("Document Type", labelFont)));
                docTable.addCell(new PdfPCell(new Phrase("Document Number", labelFont)));
                docTable.addCell(new PdfPCell(new Phrase("Status", labelFont)));

                for (DocumentResponseDTO doc : details.getDocuments()) {
                    docTable.addCell(new Phrase(doc.getDocumentType().toString(), normalFont));
                    docTable.addCell(new Phrase(doc.getDocumentNumber() != null ? doc.getDocumentNumber() : "—", normalFont));
                    docTable.addCell(new Phrase(doc.getVerificationStatus() != null ? doc.getVerificationStatus().toString() : "PENDING", normalFont));
                }
                document.add(docTable);
            } else {
                document.add(new Paragraph("No documents uploaded.", normalFont));
            }

            // --- 5. SUBJECTS ---
            addStyledSectionTitle(document, "5. ASSIGNED SUBJECTS", sectionFont);
            String subjects = details.getSubjects().stream()
                    .map(SubjectResponseDTO::getSubjectName)
                    .collect(Collectors.joining(", "));
            Paragraph subPara = new Paragraph(subjects.isEmpty() ? "None" : subjects, normalFont);
            subPara.setSpacingBefore(5);
            subPara.setSpacingAfter(15);
            document.add(subPara);

            // Footer
            Paragraph footerLine = new Paragraph("\n\n\n__________________________          __________________________", normalFont);
            footerLine.setAlignment(Element.ALIGN_CENTER);
            document.add(footerLine);
            
            Paragraph footerText = new Paragraph("Student Signature                         Admin Signature", normalFont);
            footerText.setAlignment(Element.ALIGN_CENTER);
            document.add(footerText);

            document.close();
        } catch (DocumentException e) {
            log.error("Error generating PDF: ", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addStyledSectionTitle(Document document, String title, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setBackgroundColor(new Color(52, 73, 94)); // Dark Blue-Gray Shading
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        document.add(table);
    }

    private void addTableCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null && !value.isEmpty() ? value : "—", valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
}
