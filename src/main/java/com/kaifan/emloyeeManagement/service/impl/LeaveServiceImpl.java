package com.kaifan.emloyeeManagement.service.impl;

import com.kaifan.emloyeeManagement.config.SecurityUtils;
import com.kaifan.emloyeeManagement.constants.EnumConstants;
import com.kaifan.emloyeeManagement.dto.LeaveApprovalDto;
import com.kaifan.emloyeeManagement.dto.LeaveRequestDto;
import com.kaifan.emloyeeManagement.entity.*;
import com.kaifan.emloyeeManagement.repository.*;
import com.kaifan.emloyeeManagement.service.LeaveService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public LeaveRequest submitLeave(LeaveRequestDto dto) throws Exception {
        // Get employee from JWT token (security fix)
        String employeeId = SecurityUtils.getCurrentEmployeeId();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new NoSuchElementException("LeaveType not found"));

        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();

        // Validate dates
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        // Calculate working days
        int days = calculateWorkingDays(start, end, leaveType);

        // Validate against leave type limits
        if (leaveType.getMaxDaysPerRequest() != null && days > leaveType.getMaxDaysPerRequest()) {
            throw new IllegalArgumentException("Requested days exceed maximum allowed for this leave type");
        }

        // Check for overlapping approved leaves
        boolean overlapping = leaveRequestRepository.existsApprovedForEmployeeBetween(employeeId, start, end);
        if (overlapping) {
            throw new IllegalStateException("Employee already has approved leave that overlaps this period");
        }

        // Validate substitute availability
        Employee substitute = null;
        if (dto.getSubstituteEmployeeId() != null) {
            substitute = employeeRepository.findById(dto.getSubstituteEmployeeId())
                    .orElseThrow(() -> new NoSuchElementException("Substitute not found"));
            boolean subBusy = leaveRequestRepository.existsApprovedForApproverBetween(
                    substitute.getId(), start, end);
            if (subBusy) {
                throw new IllegalStateException("Substitute is not available during requested period");
            }
        }

        // Validate and reserve balance across years
        List<LeaveBalanceSplit> balanceSplits = validateAndReserveBalance(
                employeeId, leaveType.getId(), start, end, days);

        // Create LeaveRequest
        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(employee);
        lr.setLeaveType(leaveType);
        lr.setStartDate(start);
        lr.setEndDate(end);
        lr.setNumberOfDays(days);
        lr.setSubstituteEmployee(substitute);
        lr.setStatus(EnumConstants.LeaveStatus.SUBMITTED);

        // Build approval chain
        List<String> approverIds = resolveApproverChain(employee);

        // Persist leave request
        LeaveRequest saved = leaveRequestRepository.save(lr);

        // Create approval records
        createApprovalChain(saved, approverIds);

        return saved;
    }

    @Override
    @Transactional
    public LeaveRequest approve(String leaveRequestId, LeaveApprovalDto dto) throws Exception {
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new NoSuchElementException("Leave request not found"));

        // Get approver from JWT token (security fix)
        String approverId = SecurityUtils.getCurrentEmployeeId();
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new NoSuchElementException("Approver not found"));

        // Verify this approver is authorized
        if (!approverId.equals(lr.getCurrentApproverId())) {
            throw new Exception("You are not authorized to approve this leave request");
        }

        // Find the pending approval record
        List<LeaveApproval> approvals = leaveApprovalRepository.findByLeaveRequestId(leaveRequestId);
        Optional<LeaveApproval> current = approvals.stream()
                .filter(a -> a.getApprover().getId().equals(approverId) && a.getDecision() == null)
                .findFirst();

        if (current.isEmpty()) {
            throw new IllegalStateException("No pending approval for this approver");
        }

        LeaveApproval la = current.get();
        la.setDecision(EnumConstants.ApprovalDecision.APPROVED);
        la.setComments(dto.getComments());
        la.setActionAt(LocalDateTime.now());
        leaveApprovalRepository.save(la);

        // Check if there is next approver
        Optional<LeaveApproval> next = approvals.stream()
                .filter(a -> a.getDecision() == null && !a.getId().equals(la.getId()))
                .findFirst();

        if (next.isPresent()) {
            // Move to next approver
            LeaveApproval nextApproval = next.get();
            lr.setCurrentApproverId(nextApproval.getApprover().getId());
            lr.setStatus(EnumConstants.LeaveStatus.PENDING);
            leaveRequestRepository.save(lr);
//            notificationService.notifyApprover(nextApproval.getApprover().getId(),
//                    "Leave request awaiting your approval: " + lr.getId());
        } else {
            // Final approval - confirm balance deduction
            lr.setStatus(EnumConstants.LeaveStatus.APPROVED);
            lr.setCurrentApproverId(null);
            leaveRequestRepository.save(lr);

            // Move balance from reserved to used
            confirmBalanceDeduction(lr);

//            notificationService.notifyEmployee(lr.getEmployee().getId(),
//                    "Your leave request " + lr.getId() + " has been approved");
        }

        return lr;
    }

    @Override
    @Transactional
    public LeaveRequest reject(String leaveRequestId, LeaveApprovalDto dto) throws Exception {
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new NoSuchElementException("Leave request not found"));

        // Get approver from JWT token (security fix)
        String approverId = SecurityUtils.getCurrentEmployeeId();
        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new NoSuchElementException("Approver not found"));

        // Verify authorization
        if (!approverId.equals(lr.getCurrentApproverId())) {
            throw new Exception("You are not authorized to reject this leave request");
        }

        // Find pending approval record
        List<LeaveApproval> approvals = leaveApprovalRepository.findByLeaveRequestId(leaveRequestId);
        Optional<LeaveApproval> current = approvals.stream()
                .filter(a -> a.getApprover().getId().equals(approverId) && a.getDecision() == null)
                .findFirst();

        if (current.isEmpty()) {
            throw new IllegalStateException("No pending approval for this approver");
        }

        LeaveApproval la = current.get();
        la.setDecision(EnumConstants.ApprovalDecision.REJECTED);
        la.setComments(dto.getComments());
        la.setActionAt(LocalDateTime.now());
        leaveApprovalRepository.save(la);

        // Mark leave request rejected
        lr.setStatus(EnumConstants.LeaveStatus.REJECTED);
        lr.setCurrentApproverId(null);
        lr.setLastApproverComments(dto.getComments());
        leaveRequestRepository.save(lr);

        // Release reserved balance
        releaseReservedBalance(lr);

//        notificationService.notifyEmployee(lr.getEmployee().getId(),
//                "Your leave request " + lr.getId() + " has been rejected: " + dto.getComments());

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

    // ========== CROSS-YEAR BALANCE HANDLING ==========

    /**
     * Calculate how leave days are split across years
     * This is a pure calculation based on dates - no side effects
     */
    private List<LeaveBalanceSplit> calculateBalanceSplits(
            String employeeId, String leaveTypeId,
            LocalDate startDate, LocalDate endDate, int totalDays) {

        List<LeaveBalanceSplit> splits = new ArrayList<>();
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        LeaveType leaveType = getLeaveType(leaveTypeId);

        if (startYear == endYear) {
            // Same year - simple case
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, startYear)
                    .orElseThrow(() -> new IllegalStateException(
                            "Balance not found for year " + startYear));

            splits.add(new LeaveBalanceSplit(balance.getId(), startYear,
                    startDate, endDate, totalDays));
        } else {
            // Cross-year leave - split calculation
            LocalDate yearEndDate = LocalDate.of(startYear, 12, 31);
            LocalDate nextYearStartDate = LocalDate.of(endYear, 1, 1);

            int daysInFirstYear = calculateWorkingDays(startDate, yearEndDate, leaveType);
            int daysInSecondYear = calculateWorkingDays(nextYearStartDate, endDate, leaveType);

            LeaveBalance balance1 = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, startYear)
                    .orElseThrow(() -> new IllegalStateException(
                            "Balance not found for year " + startYear));

            LeaveBalance balance2 = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, endYear)
                    .orElseThrow(() -> new IllegalStateException(
                            "Balance not found for year " + endYear));

            splits.add(new LeaveBalanceSplit(balance1.getId(), startYear,
                    startDate, yearEndDate, daysInFirstYear));
            splits.add(new LeaveBalanceSplit(balance2.getId(), endYear,
                    nextYearStartDate, endDate, daysInSecondYear));
        }

        return splits;
    }

    /**
     * Validate and reserve balance across years
     * This checks if employee has sufficient balance and reserves it
     */
    private List<LeaveBalanceSplit> validateAndReserveBalance(
            String employeeId, String leaveTypeId,
            LocalDate startDate, LocalDate endDate, int totalDays) throws Exception {

        List<LeaveBalanceSplit> splits = new ArrayList<>();
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();

        if (startYear == endYear) {
            // Same year - simple case
            LeaveBalance balance = getOrCreateBalance(employeeId, leaveTypeId, startYear);

            if (balance.getRemainingDays() < totalDays) {
                throw new Exception(
                        String.format("Insufficient leave balance for year %d. Required: %d, Available: %d",
                                startYear, totalDays, balance.getRemainingDays()));
            }

            // Reserve balance
            balance.setReservedDays(balance.getReservedDays() + totalDays);
            balance.setRemainingDays(balance.getRemainingDays() - totalDays);
            leaveBalanceRepository.save(balance);

            splits.add(new LeaveBalanceSplit(balance.getId(), startYear,
                    startDate, endDate, totalDays));

        } else {
            // Cross-year leave - split calculation
            LocalDate yearEndDate = LocalDate.of(startYear, 12, 31);
            LocalDate nextYearStartDate = LocalDate.of(endYear, 1, 1);

            // Calculate days in first year
            int daysInFirstYear = calculateWorkingDays(startDate, yearEndDate,
                    getLeaveType(leaveTypeId));

            // Calculate days in second year
            int daysInSecondYear = calculateWorkingDays(nextYearStartDate, endDate,
                    getLeaveType(leaveTypeId));

            // Validate first year balance
            LeaveBalance balance1 = getOrCreateBalance(employeeId, leaveTypeId, startYear);
            if (balance1.getRemainingDays() < daysInFirstYear) {
                throw new Exception(
                        String.format("Insufficient leave balance for year %d. Required: %d, Available: %d",
                                startYear, daysInFirstYear, balance1.getRemainingDays()));
            }

            // Validate second year balance
            LeaveBalance balance2 = getOrCreateBalance(employeeId, leaveTypeId, endYear);
            if (balance2.getRemainingDays() < daysInSecondYear) {
                throw new Exception(
                        String.format("Insufficient leave balance for year %d. Required: %d, Available: %d",
                                endYear, daysInSecondYear, balance2.getRemainingDays()));
            }

            // Reserve balance for first year
            balance1.setReservedDays(balance1.getReservedDays() + daysInFirstYear);
            balance1.setRemainingDays(balance1.getRemainingDays() - daysInFirstYear);
            leaveBalanceRepository.save(balance1);

            splits.add(new LeaveBalanceSplit(balance1.getId(), startYear,
                    startDate, yearEndDate, daysInFirstYear));

            // Reserve balance for second year
            balance2.setReservedDays(balance2.getReservedDays() + daysInSecondYear);
            balance2.setRemainingDays(balance2.getRemainingDays() - daysInSecondYear);
            leaveBalanceRepository.save(balance2);

            splits.add(new LeaveBalanceSplit(balance2.getId(), endYear,
                    nextYearStartDate, endDate, daysInSecondYear));
        }

        return splits;
    }

    /**
     * Confirm balance deduction after final approval
     * Moves days from reserved to used
     */
    private void confirmBalanceDeduction(LeaveRequest leaveRequest) {
        // Recalculate the split from the leave request data
        List<LeaveBalanceSplit> splits = calculateBalanceSplits(
                leaveRequest.getEmployee().getId(),
                leaveRequest.getLeaveType().getId(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getNumberOfDays());

        for (LeaveBalanceSplit split : splits) {
            LeaveBalance balance = leaveBalanceRepository.findById(split.getBalanceId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Balance not found: " + split.getBalanceId()));

            // Move from reserved to used
            balance.setReservedDays(balance.getReservedDays() - split.getDays());
            balance.setUsedDays(balance.getUsedDays() + split.getDays());
            leaveBalanceRepository.save(balance);
        }
    }

    /**
     * Release reserved balance when leave is rejected or cancelled
     */
    private void releaseReservedBalance(LeaveRequest leaveRequest) {
        // Recalculate the split from the leave request data
        List<LeaveBalanceSplit> splits = calculateBalanceSplits(
                leaveRequest.getEmployee().getId(),
                leaveRequest.getLeaveType().getId(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getNumberOfDays());

        for (LeaveBalanceSplit split : splits) {
            LeaveBalance balance = leaveBalanceRepository.findById(split.getBalanceId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Balance not found: " + split.getBalanceId()));

            // Release reserved days back to remaining
            balance.setReservedDays(balance.getReservedDays() - split.getDays());
            balance.setRemainingDays(balance.getRemainingDays() + split.getDays());
            leaveBalanceRepository.save(balance);
        }
    }

    /**
     * Get or create leave balance for a specific year
     */
    private LeaveBalance getOrCreateBalance(String employeeId, String leaveTypeId, int year) {
        Optional<LeaveBalance> existing = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year);

        if (existing.isPresent()) {
            return existing.get();
        }

        // Create new balance for this year
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new NoSuchElementException("Leave type not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        LeaveBalance newBalance = new LeaveBalance();
        newBalance.setEmployee(employee);
        newBalance.setLeaveType(leaveType);
        newBalance.setYear(year);
        newBalance.setTotalDays(leaveType.getDefaultDaysPerYear());
        newBalance.setUsedDays(0);
        newBalance.setReservedDays(0);
        newBalance.setRemainingDays(leaveType.getDefaultDaysPerYear());

        // Handle carry-forward from previous year
        handleCarryForward(newBalance, employeeId, leaveTypeId, year, leaveType);

        return leaveBalanceRepository.save(newBalance);
    }

    /**
     * Handle carry-forward logic from previous year
     * IMPORTANT: Only carry forward REMAINING days, not reserved or used days
     */
    private void handleCarryForward(LeaveBalance newBalance, String employeeId,
                                    String leaveTypeId, int year, LeaveType leaveType) {
        if (!leaveType.getAllowCarryForward()) {
            return;
        }

        int previousYear = year - 1;
        Optional<LeaveBalance> previousBalance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, previousYear);

        if (previousBalance.isPresent()) {
            LeaveBalance prevBalance = previousBalance.get();

            // CRITICAL: Use remainingDays, NOT (totalDays - usedDays)
            // remainingDays already excludes reserved days from pending requests
            int availableForCarryForward = prevBalance.getRemainingDays();

            if (availableForCarryForward > 0) {
                // Calculate carried forward days with limit
                int carriedDays = availableForCarryForward;
                if (leaveType.getMaxCarryForwardDays() != null) {
                    carriedDays = Math.min(carriedDays, leaveType.getMaxCarryForwardDays());
                }

                newBalance.setCarriedForwardDays(carriedDays);
                newBalance.setTotalDays(newBalance.getTotalDays() + carriedDays);
                newBalance.setRemainingDays(newBalance.getRemainingDays() + carriedDays);
            }
        }
    }

    /**
     * Calculate working days excluding weekends and holidays
     */
    private int calculateWorkingDays(LocalDate start, LocalDate end, LeaveType leaveType) {
//        if (!leaveType.getExcludeWeekends() && !leaveType.getExcludeHolidays()) {
//            // Include all days
//            return (int) ChronoUnit.DAYS.between(start, end) + 1;
//        }

        int days = 0;
        LocalDate current = start;

        while (!current.isAfter(end)) {
            boolean isWorkingDay = true;

            // Check weekends (Friday and Saturday for Kuwait/Middle East)

            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
                isWorkingDay = false;
            }


            // Check holidays (you would implement this based on your HolidayCalendar)
            if (isWorkingDay) {
                // TODO: Check against HolidayCalendar entity
                // if (holidayCalendarService.isHoliday(current)) {
                //     isWorkingDay = false;
                // }
            }

            if (isWorkingDay) {
                days++;
            }

            current = current.plusDays(1);
        }

        return days;
    }

    // ========== HELPER METHODS ==========

    private LeaveType getLeaveType(String leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new NoSuchElementException("Leave type not found"));
    }

    private void createApprovalChain(LeaveRequest leaveRequest, List<String> approverIds) {
        for (int i = 0; i < approverIds.size(); i++) {
            String approverId = approverIds.get(i);
            LeaveApproval la = new LeaveApproval();
            la.setId(UUID.randomUUID().toString());
            la.setLeaveRequest(leaveRequest);
            Employee approver = employeeRepository.findById(approverId).orElseThrow();
            la.setApprover(approver);
            la.setDecision(null);
            la.setComments(null);
            leaveApprovalRepository.save(la);

            if (i == 0) {
                leaveRequest.setCurrentApproverId(approverId);
                leaveRequestRepository.save(leaveRequest);
//                notificationService.notifyApprover(approverId,
//                        "New leave request to approve: " + leaveRequest.getId());
            }
        }
    }

    private List<String> resolveApproverChain(Employee employee) {
        List<String> approvers = new ArrayList<>();

        if (employee.getManagerId() != null) {
            approvers.add(employee.getManagerId());

            Employee manager = employeeRepository.findById(employee.getManagerId()).orElse(null);
            if (manager != null && manager.getManagerId() != null) {
                approvers.add(manager.getManagerId());
            }
        }

        // Add HR approver
//        List<Employee> hrEmployees = employeeRepository.findByRole("HR");
//        if (!hrEmployees.isEmpty()) {
//            approvers.add(hrEmployees.get(0).getId());
//        }

        return approvers.stream().distinct().collect(Collectors.toList());
    }

    // ========== INNER CLASS ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveBalanceSplit {
        private String balanceId;
        private int year;
        private LocalDate startDate;
        private LocalDate endDate;
        private int days;
    }

}
