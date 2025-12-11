package com.kaifan.emloyeeManagement.controller;

import com.kaifan.emloyeeManagement.constants.MessageConstants;
import com.kaifan.emloyeeManagement.dto.LeaveBalanceResponseDto;
import com.kaifan.emloyeeManagement.dto.ResponseDto;
import com.kaifan.emloyeeManagement.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.Year;

@RestController
@RequestMapping("/api/leave-balances")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @GetMapping
    public ResponseDto getLeaveBalancesForEmployee(
            @RequestParam(required = false) Integer year) {
        
        // If year is not provided, use current year
        int targetYear = (year != null) ? year : Year.now().getValue();
        
        LeaveBalanceResponseDto leaveBalanceResponseDto = leaveBalanceService.getLeaveBalanceForEmployee(targetYear);
        if( leaveBalanceResponseDto != null ) {
            return new ResponseDto(true, leaveBalanceResponseDto, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_NOT_FOUND);
    }
}
