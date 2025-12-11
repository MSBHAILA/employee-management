package com.kaifan.emloyeeManagement.dto;

import com.kaifan.emloyeeManagement.entity.Employee;
import com.kaifan.emloyeeManagement.entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDto {
    private String id;

    private String employee;

    private String leaveType;

    private String year;

    private String totalDays;

    private String usedDays;

    private String remainingDays;

    private String reservedDays;

    private String carriedForwardDays;

    private String createdAt;

    private String updatedAt;
}