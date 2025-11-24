package com.kaifan.emloyeeManagement.controller;

import com.kaifan.emloyeeManagement.constants.MessageConstants;
import com.kaifan.emloyeeManagement.dto.LeaveApprovalDto;
import com.kaifan.emloyeeManagement.dto.LeaveRequestDto;
import com.kaifan.emloyeeManagement.dto.ResponseDto;
import com.kaifan.emloyeeManagement.entity.LeaveRequest;
import com.kaifan.emloyeeManagement.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing leave requests.
 */
@RestController
@RequestMapping("/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    /**
     * Submit a new leave request.
     *
     * @param dto the leave request details
     * @return the created leave request
     */
    @PostMapping(path = "/submit")
    public ResponseDto submitLeave(@RequestBody LeaveRequestDto dto) {
        try {
            LeaveRequest leaveRequest = leaveService.submitLeave(dto);
            return new ResponseDto(true, leaveRequest, MessageConstants.RECORD_CREATED_SUCCESSFULLY);
        } catch (Exception e) {
            return new ResponseDto(false, null, e.getMessage());
        }
    }

    /**
     * Approve a leave request.
     *
     * @param id  the ID of the leave request
     * @param dto the approval details
     * @return the approved leave request
     */
    @PostMapping("/{id}/approve")
    public ResponseDto approveLeaveRequest(
            @PathVariable String id,
            @RequestBody LeaveApprovalDto dto) {
        try {
            LeaveRequest leaveRequest = leaveService.approve(id, dto);
            return new ResponseDto(true, leaveRequest, MessageConstants.RECORD_UPDATED_SUCCESSFULLY);
        } catch (Exception e) {
            return new ResponseDto(false, null, e.getMessage());
        }
    }

    /**
     * Reject a leave request.
     *
     * @param id  the ID of the leave request
     * @param dto the rejection details
     * @return the rejected leave request
     */
    @PostMapping("/{id}/reject")
    public ResponseDto rejectLeaveRequest(
            @PathVariable String id,
            @RequestBody LeaveApprovalDto dto) {
        try {
            LeaveRequest leaveRequest = leaveService.reject(id, dto);
            return new ResponseDto(true, leaveRequest, MessageConstants.RECORD_UPDATED_SUCCESSFULLY);
        } catch (Exception e) {
            return new ResponseDto(false, null, e.getMessage());
        }
    }

    /**
     * Get all leave requests for an employee.
     *
     * @param employeeId the ID of the employee
     * @return list of leave requests
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseDto getLeaveRequestsByEmployee(@PathVariable String employeeId) {
        try {
            List<LeaveRequest> leaveRequests = leaveService.getRequestsForEmployee(employeeId);
            return new ResponseDto(true, leaveRequests, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
        } catch (Exception e) {
            return new ResponseDto(false, null, e.getMessage());
        }
    }

    /**
     * Get a leave request by ID.
     *
     * @param id the ID of the leave request
     * @return the leave request
     */
    @GetMapping("/{id}")
    public ResponseDto getLeaveRequestById(@PathVariable String id) {
        try {
            LeaveRequest leaveRequest = leaveService.getById(id);
            if (leaveRequest != null) {
                return new ResponseDto(true, leaveRequest, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
            } else {
                return new ResponseDto(false, null, MessageConstants.RECORD_NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseDto(false, null, e.getMessage());
        }
    }
}
