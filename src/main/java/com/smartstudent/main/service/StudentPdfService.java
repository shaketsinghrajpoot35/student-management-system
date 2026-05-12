package com.smartstudent.main.service;

import java.io.ByteArrayInputStream;

public interface StudentPdfService {
    ByteArrayInputStream generateStudentRegistrationForm(Long studentId);
}
