package com.smartstudent.main.controller;

import com.smartstudent.main.dto.request.DocumentDTO;
import com.smartstudent.main.dto.response.ApiResponseDTO;
import com.smartstudent.main.dto.response.DocumentResponseDTO;
import com.smartstudent.main.service.StudentDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class StudentDocumentController {

    private final StudentDocumentService documentService;

    /**
     * POST /api/documents/upload
     * multipart: 'studentId', 'documentType', optional metadata, 'file'
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<DocumentResponseDTO>> uploadDocument(
            @RequestParam Long studentId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper) throws Exception {

        DocumentDTO dto = objectMapper.readValue(metadataJson, DocumentDTO.class);
        DocumentResponseDTO response = documentService.uploadDocument(studentId, dto, file);
        log.info("Document uploaded for student {}: {}", studentId, dto.getDocumentType());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Document uploaded successfully", response));
    }

    /**
     * GET /api/documents/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponseDTO<List<DocumentResponseDTO>>> getDocuments(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Documents retrieved",
                documentService.getDocumentsByStudentId(studentId)));
    }

    /**
     * PUT /api/documents/{id}
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<DocumentResponseDTO>> updateDocument(
            @PathVariable Long id,
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper) throws Exception {

        DocumentDTO dto = objectMapper.readValue(metadataJson, DocumentDTO.class);
        DocumentResponseDTO response = documentService.updateDocument(id, dto, file);
        return ResponseEntity.ok(ApiResponseDTO.success("Document updated", response));
    }

    /**
     * DELETE /api/documents/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Document deleted"));
    }

    /**
     * GET /api/documents/download/{id}
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Resource resource = documentService.downloadDocument(id);
        String contentType = documentService.getDocumentContentType(id);
        String fileName = documentService.getDocumentFileName(id);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }
}
