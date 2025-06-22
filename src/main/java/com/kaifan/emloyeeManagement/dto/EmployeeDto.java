package com.kaifan.emloyeeManagement.dto;

import com.kaifan.emloyeeManagement.constants.EnumConstants.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private String id;
    private String adUsername;
    private String employeeNumber;
    private String fullNameAr;
    private String fullNameEn;
    private String gender;
    private LocalDate dateOfBirth;
    private String civilId;
    private String nationality;
    private String phonePrimary;
    private String phoneSecondary;
    private String residentialAddress;
    private String religion;
    private String bankName;
    private String bankAccountNumber;
    private String maritalStatus;
    private String educationLevel;
    private String jobTitle;
    private String location;
    private String biometricType;
    private String placeOfBirth;
    private String insuranceStatus;
    private String laborSupportStatus;
    private String hrTitle;
    private String department;
    private String positionId;
    private String managerId;
    private LocalDate employmentDate;
    private Integer previousServiceDuration = 0;
    private Integer restDays = 30;
    private Status employmentStatus = Status.ACTIVE;
    private Status salaryStatus = Status.ACTIVE;
    private LocalDate lastBonusDate;
    private String photoUrl;
    private String barcode;
    private Boolean idCardPrinted = false;
    private String notes;
    private String positionName;
    private String managerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
