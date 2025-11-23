package com.kaifan.emloyeeManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_balance")
public class LeaveBalance {
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

    @Column(nullable = false)
    private Integer year;  // 2025, 2026, etc.

    @Column(nullable = false)
    private Integer totalDays;  // Allocated for this year

    @Column(nullable = false)
    private Integer usedDays;  // Already consumed

    @Column(nullable = false)
    private Integer remainingDays;  // Available = totalDays - usedDays - reservedDays

    @Column(nullable = false)
    private Integer reservedDays;  // Reserved for future use

    // For carry-forward tracking
    @Column
    private Integer carriedForwardDays;  // Days brought from previous year

    @CreatedDate
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
