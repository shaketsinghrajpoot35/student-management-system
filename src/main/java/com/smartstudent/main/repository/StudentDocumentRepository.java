package com.smartstudent.main.repository;

import com.smartstudent.main.entity.StudentDocument;
import com.smartstudent.main.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Long> {
    List<StudentDocument> findByStudentId(Long studentId);
    Optional<StudentDocument> findByStudentIdAndDocumentType(Long studentId, DocumentType documentType);
    void deleteByStudentId(Long studentId);
}
