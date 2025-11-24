//package com.kaifan.emloyeeManagement.service.impl;
//
//import com.kaifan.emloyeeManagement.entity.Employee;
//import com.kaifan.emloyeeManagement.entity.LeaveBalance;
//import com.kaifan.emloyeeManagement.entity.LeaveType;
//import com.kaifan.emloyeeManagement.repository.EmployeeRepository;
//import com.kaifan.emloyeeManagement.repository.LeaveBalanceRepository;
//import com.kaifan.emloyeeManagement.repository.LeaveTypeRepository;
//import com.kaifan.emloyeeManagement.service.LeaveBalanceService;
//import jakarta.ws.rs.NotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//@Service
//public class LeaveBalanceServiceImpl implements LeaveBalanceService {
//
//    @Autowired
//    private LeaveBalanceRepository leaveBalanceRepository;
//
//    @Autowired
//    private LeaveTypeRepository leaveTypeRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    /**
//     * Get or create leave balance for employee, leave type, and year
//     */
//    @Override
//    public LeaveBalance getOrCreateBalance(String employeeId, String leaveTypeId, int year) {
//        return leaveBalanceRepository
//                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
//                .orElseGet(() -> createBalanceForYear(employeeId, leaveTypeId, year));
//    }
//
//    @Override
//    public LeaveBalance createBalanceForYear(String employeeId, String leaveTypeId, int year) {
//        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
//                .orElseThrow(() -> new NotFoundException("Leave type not found"));
//
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new NotFoundException("Employee not found"));
//        LeaveBalance balance = new LeaveBalance();
//        balance.setEmployee(employee); // or fetch from repo
//        balance.setLeaveType(leaveType);
//        balance.setYear(year);
//        balance.setTotalDays(leaveType.getMaxDaysPerYear());
//        balance.setUsedDays(0);
//        balance.setRemainingDays(leaveType.getMaxDaysPerYear());
//
//        // Handle carry-forward from previous year
//        int previousYear = year - 1;
//        Optional<LeaveBalance> previousBalance = leaveBalanceRepository
//                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, previousYear);
//
//        if (previousBalance.isPresent() && leaveType.getAllowCarryForward()) {
//            int carriedDays = Math.min(
//                    previousBalance.get().getRemainingDays(),
//                    leaveType.getMaxCarryForwardDays() != null ?
//                            leaveType.getMaxCarryForwardDays() : Integer.MAX_VALUE
//            );
//            balance.setCarriedForwardDays(carriedDays);
//            balance.setTotalDays(balance.getTotalDays() + carriedDays);
//            balance.setRemainingDays(balance.getRemainingDays() + carriedDays);
//
//            // Set expiry for carried forward days (e.g., March 31st)
//            if (leaveType.getCarryForwardExpiryMonths() != null) {
//                balance.setExpiryDate(LocalDate.of(year, 3, 31)); // Example: Q1 end
//            }
//        }
//
//        return leaveBalanceRepository.save(balance);
//    }
//}