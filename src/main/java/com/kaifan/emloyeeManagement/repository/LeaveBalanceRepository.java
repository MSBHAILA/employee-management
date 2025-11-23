package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, String> {
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(String employeeId, String leaveTypeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(String employeeId, String leaveTypeId, int year);
}
