package com.kaifan.emloyeeManagement.dto;

import com.kaifan.emloyeeManagement.constants.EnumConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubstitutionDto {
    private String id;
    private String originalEmployeeId;
    private String substituteEmployeeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
    private EnumConstants.Status status; // e.g., PENDING, ACTIVE, COMPLETED, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
