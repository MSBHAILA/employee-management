package com.kaifan.emloyeeManagement.service;

import com.kaifan.emloyeeManagement.dto.LeaveApprovalDto;
import com.kaifan.emloyeeManagement.dto.LeaveRequestDto;
import com.kaifan.emloyeeManagement.entity.LeaveRequest;

import java.util.List;

public interface LeaveService {
    LeaveRequest submitLeave(LeaveRequestDto dto) throws Exception;
    LeaveRequest approve(String leaveRequestId, LeaveApprovalDto dto) throws Exception;
    LeaveRequest reject(String leaveRequestId, LeaveApprovalDto dto) throws Exception;
    List<LeaveRequest> getRequestsForEmployee(String employeeId);
    LeaveRequest getById(String id);
}
