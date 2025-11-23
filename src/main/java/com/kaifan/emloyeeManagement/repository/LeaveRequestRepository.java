package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

    @Query("SELECT CASE WHEN COUNT(l)>0 THEN true ELSE false END FROM LeaveRequest l WHERE l.employee.id = :employeeId AND l.status = 'APPROVED' AND NOT (l.endDate < :startDate OR l.startDate > :endDate)")
    boolean existsApprovedForEmployeeBetween(@Param("employeeId") String employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(l)>0 THEN true ELSE false END FROM LeaveRequest l WHERE l.employee.id = :employeeId AND l.status = 'APPROVED' AND NOT (l.endDate < :startDate OR l.startDate > :endDate)")
    boolean existsApprovedForApproverBetween(@Param("employeeId") String employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
