package com.kaifan.emloyeeManagement.entity;

import com.kaifan.emloyeeManagement.constants.EnumConstants.LeaveStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_request")
public class LeaveRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer numberOfDays;

    @ManyToOne
    @JoinColumn(name = "substitute_employee_id")
    private Employee substituteEmployee;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.SUBMITTED;

    private String currentApproverId; // store id of approver who should act next

    private String lastApproverComments;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
