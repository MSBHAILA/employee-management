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

    private Employee employee;

    private LeaveType leaveType;

    private Integer remainingDays;
}