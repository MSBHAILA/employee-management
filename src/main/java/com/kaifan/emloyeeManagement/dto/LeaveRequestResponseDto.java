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
public class LeaveRequestResponseDto {

    private String id;
    private String employeeId;
    private String leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfDays;
    private String substituteEmployeeId;
    private String status;
    private String currentApproverId;
    private String lastApproverComments;
}
