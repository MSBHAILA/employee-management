package com.kaifan.emloyeeManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDto {
    private String leaveTypeId;
    private String leaveTypeCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String substituteEmployeeId;
    private String reason;
}
