package com.kaifan.emloyeeManagement.service;

import com.kaifan.emloyeeManagement.dto.EmployeeDocumentDto;
import com.kaifan.emloyeeManagement.entity.EmployeeDocument;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeDocumentService {
    EmployeeDocumentDto uploadDocument(String employeeId, MultipartFile file, EmployeeDocumentDto metadata);

    Resource downloadDocument(String documentId);

    void deleteDocument(String documentId);

    List<EmployeeDocumentDto> getDocumentsByEmployee(String employeeId);
}

