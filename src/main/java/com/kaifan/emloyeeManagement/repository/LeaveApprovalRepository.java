package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.LeaveApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApprovalRepository extends JpaRepository<LeaveApproval, String> {
    List<LeaveApproval> findByLeaveRequestIdOrderByActionAtAsc(String leaveRequestId);
    List<LeaveApproval> findByLeaveRequestId(String leaveRequestId);
}
