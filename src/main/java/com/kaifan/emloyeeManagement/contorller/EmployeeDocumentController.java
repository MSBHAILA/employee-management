package com.kaifan.emloyeeManagement.contorller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaifan.emloyeeManagement.constants.MessageConstants;
import com.kaifan.emloyeeManagement.dto.EmployeeDocumentDto;
import com.kaifan.emloyeeManagement.dto.ResponseDto;
import com.kaifan.emloyeeManagement.service.EmployeeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for managing employee documents.
 */
@RestController
@RequestMapping("/documents")
public class EmployeeDocumentController {

    @Autowired
    private EmployeeDocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Upload a document for an employee.
     *
     * @param employeeId the ID of the employee
     * @param file       the document file
     * @param metadataJson   the document metadata
     * @return the uploaded document details
     */
    @PostMapping(value = "/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto uploadDocument(
            @PathVariable String employeeId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadataJson) throws JsonProcessingException {

        EmployeeDocumentDto metadata = objectMapper.readValue(metadataJson, EmployeeDocumentDto.class);
        EmployeeDocumentDto savedDocument = documentService.uploadDocument(employeeId, file, metadata);
        return new ResponseDto(true, savedDocument, MessageConstants.RECORD_CREATED_SUCCESSFULLY);
    }

    /**
     * Download a document.
     *
     * @param employeeId the ID of the employee (not used in service but kept for RESTful URL)
     * @param documentId the ID of the document to download
     * @return the document as a downloadable resource
     */
    @GetMapping("/{employeeId}/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable String employeeId,
            @PathVariable String documentId) {

        Resource resource = documentService.downloadDocument(documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Delete a document.
     *
     * @param employeeId the ID of the employee (not used in service but kept for RESTful URL)
     * @param documentId the ID of the document to delete
     * @return a response indicating success or failure
     */
    @DeleteMapping("/{documentId}")
    public ResponseDto deleteDocument(
            @PathVariable String employeeId,
            @PathVariable String documentId) {

        documentService.deleteDocument(documentId);
        return new ResponseDto(true, null, MessageConstants.RECORD_DELETED_SUCCESSFULLY);
    }

    /**
     * Get all documents for an employee.
     *
     * @param employeeId the ID of the employee
     * @return list of document DTOs
     */
    @GetMapping
    public ResponseDto getDocumentsByEmployee(
            @PathVariable String employeeId) {

        List<EmployeeDocumentDto> documents = documentService.getDocumentsByEmployee(employeeId);
        return new ResponseDto(true, documents, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
    }
}
