package com.kaifan.emloyeeManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApprovalDto{
    private String id;

    private String leaveRequest;

    private String approverId;

    private String decision;

    private String comments;

    private LocalDateTime actionAt;
}
