package com.kaifan.emloyeeManagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employee_document")
public class EmployeeDocument {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "upload_path")
    private String uploadPath;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "place_of_issue")
    private String placeOfIssue;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "upload_time", updatable = false)
    private LocalDateTime uploadTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
