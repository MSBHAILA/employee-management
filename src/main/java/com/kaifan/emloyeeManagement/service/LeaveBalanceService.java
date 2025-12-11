package com.kaifan.emloyeeManagement.service;

import com.kaifan.emloyeeManagement.dto.LeaveBalanceResponseDto;
import com.kaifan.emloyeeManagement.entity.LeaveBalance;

public interface LeaveBalanceService {
    LeaveBalance getOrCreateBalance(String employeeId, String leaveTypeId, int year);

    LeaveBalance createBalanceForYear(String employeeId, String leaveTypeId, int year);

    LeaveBalanceResponseDto getLeaveBalanceForEmployee(int year);
}
