package com.kaifan.emloyeeManagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "leave_type")
public class LeaveType {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(unique = true, nullable = false, name = "name")
    private String name;

    @Column(name = "default_days_per_year", nullable = false)
    private Integer defaultDaysPerYear; // Default allocation (e.g., 22 for annual)

    @Column(name = "max_days_per_year")
    private Integer maxDaysPerYear;

    @Column(name = "max_days_per_request")
    private Integer maxDaysPerRequest;

    @Column(name = "allow_carry_forward", nullable = false)
    private Boolean allowCarryForward = false;

    @Column(name = "carry_forward_expiry_months")
    private Integer carryForwardExpiryMonths;

    @Column(name = "max_carry_forward_days")
    private Integer maxCarryForwardDays;

    @Column(name = "min_service_months")
    private Integer minServiceMonths;
}

