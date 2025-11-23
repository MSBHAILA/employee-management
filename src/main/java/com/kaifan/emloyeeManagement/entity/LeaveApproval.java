package com.kaifan.emloyeeManagement.entity;

import com.kaifan.emloyeeManagement.constants.EnumConstants.ApprovalDecision;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_approval")
public class LeaveApproval {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "leave_request_id")
    private LeaveRequest leaveRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "approver_id")
    private Employee approver;

    @Enumerated(EnumType.STRING)
    private ApprovalDecision decision;

    private String comments;

    @CreationTimestamp
    private LocalDateTime actionAt;
}
