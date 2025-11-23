package com.kaifan.emloyeeManagement.service;

import com.kaifan.emloyeeManagement.dto.LeaveApprovalDto;
import com.kaifan.emloyeeManagement.dto.LeaveRequestDto;
import com.kaifan.emloyeeManagement.entity.LeaveRequest;

import java.util.List;

public interface LeaveService {
    LeaveRequest submitLeave(String employeeId, LeaveRequestDto dto);
    LeaveRequest approve(String leaveRequestId, LeaveApprovalDto dto);
    LeaveRequest reject(String leaveRequestId, LeaveApprovalDto dto);
    List<LeaveRequest> getRequestsForEmployee(String employeeId);
    LeaveRequest getById(String id);
}
