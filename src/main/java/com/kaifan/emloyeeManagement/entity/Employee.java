package com.kaifan.emloyeeManagement.entity;

import com.kaifan.emloyeeManagement.constants.EnumConstants.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "ad_username", unique = true)
    private String adUsername;

    @Column(name = "employee_number", unique = true, nullable = false)
    private String employeeNumber;

//    @Column(name = "employee_file_number", unique = true, nullable = false)
//    private String employeeFileNumber;

    // Personal Information
    @Column(name = "full_name_ar", nullable = false)
    private String fullNameAr;

    @Column(name = "full_name_en", nullable = false)
    private String fullNameEn;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "civil_id", unique = true, nullable = false)
    private String civilId;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "phone_primary")
    private String phonePrimary;

    @Column(name = "phone_secondary")
    private String phoneSecondary;

    @Column(name = "residential_address")
    private String residentialAddress;

    @Column(name = "religion")
    private String religion;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "insurance_status")
    private String insuranceStatus;

    @Column(name = "labor_support_status")
    private String laborSupportStatus;

    // Employment Information
    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "hr_title")
    private String hrTitle;

    @Column(name = "department")
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;

    @Column(name = "manager_id")
    private String managerId;

    @Column(name = "employment_date", nullable = false)
    private LocalDate employmentDate;

    @Column(name = "location")
    private String location;

    @Column(name = "previous_service_duration")
    private Integer previousServiceDuration = 0;

    @Column(name = "biometric_type")
    private String biometricType;

    @Column(name = "rest_days")
    private Integer restDays = 30;

    // Status Information
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private Status employmentStatus = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_status")
    private Status salaryStatus = Status.ACTIVE;

    @Column(name = "last_bonus_date")
    private LocalDate lastBonusDate;

    // ID Card Information
    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "id_card_printed")
    private Boolean idCardPrinted = false;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}


