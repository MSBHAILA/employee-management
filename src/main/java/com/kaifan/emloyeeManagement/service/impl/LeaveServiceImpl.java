package com.kaifan.emloyeeManagement.service.impl;

import com.kaifan.emloyeeManagement.constants.EnumConstants;
import com.kaifan.emloyeeManagement.dto.LeaveApprovalDto;
import com.kaifan.emloyeeManagement.dto.LeaveRequestDto;
import com.kaifan.emloyeeManagement.entity.*;
import com.kaifan.emloyeeManagement.repository.*;
import com.kaifan.emloyeeManagement.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveApprovalRepository leaveApprovalRepository;


    @Override
    @Transactional
    public LeaveRequest submitLeave(String employeeId, LeaveRequestDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new NoSuchElementException("LeaveType not found"));

        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();
        if (end.isBefore(start)) throw new IllegalArgumentException("End date before start date");

        int days = (int) ChronoUnit.DAYS.between(start, end) + 1;

        // Enforce maxDaysPerYear on LeaveType if set
        if (leaveType.getMaxDaysPerYear() != null && days > leaveType.getMaxDaysPerYear()) {
            throw new IllegalArgumentException("Requested days exceed maximum allowed for this leave type");
        }

        // Check employee existing approved leaves overlap
        boolean overlapping = leaveRequestRepository.existsApprovedForEmployeeBetween(employeeId, start, end);
        if (overlapping) throw new IllegalStateException("Employee already has approved leave that overlaps this period");

        // Check substitute availability
        Employee substitute = null;
        if (dto.getSubstituteEmployeeId() != null) {
            substitute = employeeRepository.findById(dto.getSubstituteEmployeeId()).orElseThrow(() -> new NoSuchElementException("Substitute not found"));
            boolean subBusy = leaveRequestRepository.existsApprovedForApproverBetween(substitute.getId(), start, end);
            if (subBusy) throw new IllegalStateException("Substitute is not available during requested period");
        }

        // Check LeaveBalance for the year(s). For simplicity, assume same-year leave. Production: handle cross-year splits.
        int year = start.getYear();
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveType.getId())
                .orElseThrow(() -> new IllegalStateException("Leave balance not configured for this employee and year"));

        if (balance.getRemainingDays() < days) throw new IllegalStateException("Insufficient leave balance");

        // Create LeaveRequest
        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(employee);
        lr.setLeaveType(leaveType);
        lr.setStartDate(start);
        lr.setEndDate(end);
        lr.setNumberOfDays(days);
        lr.setSubstituteEmployee(substitute);
        lr.setStatus(EnumConstants.LeaveStatus.SUBMITTED);

        // Build approval chain (multi-level): 1) Team leader (managerId), 2) manager's manager, 3) HR (find by role? here we stub)
        List<String> approverIds = resolveApproverChain(employee);

        // persist leave request first to get id for approvals
        LeaveRequest saved = leaveRequestRepository.save(lr);

        // Create LeaveApproval rows representing the chain, first is PENDING, others WAITING
        for (int i = 0; i < approverIds.size(); i++) {
            String approverId = approverIds.get(i);
            LeaveApproval la = new LeaveApproval();
            la.setId(UUID.randomUUID().toString());
            la.setLeaveRequest(saved);
            Employee approver = employeeRepository.findById(approverId).orElseThrow();
            la.setApprover(approver);
            la.setDecision(null);
            la.setComments(null);
            leaveApprovalRepository.save(la);
            if (i == 0) {
                // set current approver on leave request
                saved.setCurrentApproverId(approverId);
                leaveRequestRepository.save(saved);
//                notificationService.notifyApprover(approverId, "New leave request to approve: " + saved.getId());
            }
        }

        return saved;
    }

    @Override
    @Transactional
    public LeaveRequest approve(String leaveRequestId, LeaveApprovalDto dto) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId).orElseThrow();
        Employee approver = employeeRepository.findById(dto.getApproverId()).orElseThrow();

        // find the pending approval record for this approver and leave
        List<LeaveApproval> approvals = leaveApprovalRepository.findByLeaveRequestId(leaveRequestId);
        Optional<LeaveApproval> current = approvals.stream()
                .filter(a -> a.getApprover().getId().equals(approver.getId()) && a.getDecision() == null)
                .findFirst();

        if (current.isEmpty()) throw new IllegalStateException("No pending approval for this approver");

        LeaveApproval la = current.get();
        la.setDecision(EnumConstants.ApprovalDecision.APPROVED);
        la.setComments(dto.getComments());
        leaveApprovalRepository.save(la);

        // check if there is next approver
        List<LeaveApproval> sorted = approvals.stream()
                .sorted(Comparator.comparing(LeaveApproval::getActionAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        // Determine next approver: the first approval with decision == null (excluding current)
        Optional<LeaveApproval> next = approvals.stream()
                .filter(a -> a.getDecision() == null && !a.getId().equals(la.getId()))
                .findFirst();

        if (next.isPresent()) {
            // set currentApproverId to next
            LeaveApproval nextApproval = next.get();
            lr.setCurrentApproverId(nextApproval.getApprover().getId());
            lr.setStatus(EnumConstants.LeaveStatus.PENDING);
            leaveRequestRepository.save(lr);
//            notificationService.notifyApprover(nextApproval.getApprover().getId(), "Leave request awaiting your approval: " + lr.getId());
        } else {
            // final approval reached
            lr.setStatus(EnumConstants.LeaveStatus.APPROVED);
            lr.setCurrentApproverId(null);
            leaveRequestRepository.save(lr);

            // Deduct balance
            int year = lr.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(lr.getEmployee().getId(), lr.getLeaveType().getId())
                    .orElseThrow(() -> new IllegalStateException("Leave balance not configured"));
            if (balance.getRemainingDays() < lr.getNumberOfDays()) throw new IllegalStateException("Insufficient balance at approval time");
            balance.setRemainingDays(balance.getRemainingDays() - lr.getNumberOfDays());
            leaveBalanceRepository.save(balance);

//            notificationService.notifyEmployee(lr.getEmployee().getId(), "Your leave request " + lr.getId() + " has been approved");
        }

        return lr;
    }

    @Override
    @Transactional
    public LeaveRequest reject(String leaveRequestId, LeaveApprovalDto dto) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId).orElseThrow();
        Employee approver = employeeRepository.findById(dto.getApproverId()).orElseThrow();

        // find pending approval record
        List<LeaveApproval> approvals = leaveApprovalRepository.findByLeaveRequestId(leaveRequestId);
        Optional<LeaveApproval> current = approvals.stream()
                .filter(a -> a.getApprover().getId().equals(approver.getId()) && a.getDecision() == null)
                .findFirst();

        if (current.isEmpty()) throw new IllegalStateException("No pending approval for this approver");

        LeaveApproval la = current.get();
        la.setDecision(EnumConstants.ApprovalDecision.REJECTED);
        la.setComments(dto.getComments());
        leaveApprovalRepository.save(la);

        // mark leave request rejected
        lr.setStatus(EnumConstants.LeaveStatus.REJECTED);
        lr.setCurrentApproverId(null);
        lr.setLastApproverComments(dto.getComments());
        leaveRequestRepository.save(lr);

//        notificationService.notifyEmployee(lr.getEmployee().getId(), "Your leave request " + lr.getId() + " has been rejected: " + dto.getComments());

        return lr;
    }

    @Override
    public List<LeaveRequest> getRequestsForEmployee(String employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    @Override
    public LeaveRequest getById(String id) {
        return leaveRequestRepository.findById(id).orElseThrow();
    }

    // Helper to resolve approver chain: team leader -> team leader's manager -> HR
    // This is simplistic; in production you may load approvers based on roles/organizational config.
    private List<String> resolveApproverChain(Employee employee) {
        List<String> approvers = new ArrayList<>();
        if (employee.getManagerId() != null) {
            approvers.add(employee.getManagerId());
            // manager of manager
            Employee manager = employeeRepository.findById(employee.getManagerId()).orElse(null);
            if (manager != null && manager.getManagerId() != null) {
                approvers.add(manager.getManagerId());
            }
        }
        // Add HR user(s) - not present in Employee entity; for demo, fallback: find an employee with hrTitle == "HR"
        Optional<Employee> hr = employeeRepository.findAll().stream().filter(e -> "HR".equalsIgnoreCase(e.getHrTitle())).findFirst();
        hr.ifPresent(h -> approvers.add(h.getId()));

        // Remove duplicates
        return approvers.stream().distinct().collect(Collectors.toList());
    }
}
