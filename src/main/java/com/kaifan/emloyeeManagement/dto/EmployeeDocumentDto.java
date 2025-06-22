package com.kaifan.emloyeeManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocumentDto {
    private String id;
    private String employeeId;
    private String documentType;
    private String fileName;
    private String originalName;
    private String uploadPath;
    private String referenceNumber;
    private String documentNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String placeOfIssue;
    private String licenseNumber;
    private String notes;
    private LocalDateTime uploadTime;
    private LocalDateTime updatedAt;
}
