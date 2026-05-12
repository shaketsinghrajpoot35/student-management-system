package com.smartstudent.main.service;

import java.io.ByteArrayInputStream;

public interface StudentExportService {
    ByteArrayInputStream exportStudentsToCsv(String name, String samagraId, String className, String admissionNumber, String stream);
}
