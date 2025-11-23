package com.kaifan.emloyeeManagement.controller;

import com.kaifan.emloyeeManagement.dto.LeaveApprovalDto;
import com.kaifan.emloyeeManagement.dto.LeaveRequestDto;
import com.kaifan.emloyeeManagement.entity.LeaveRequest;
import com.kaifan.emloyeeManagement.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping
    public ResponseEntity<?> submit(@RequestParam("employeeId") String employeeId, @RequestBody LeaveRequestDto dto) {
        LeaveRequest lr = leaveService.submitLeave(employeeId, dto);
        return ResponseEntity.created(URI.create("/api/leaves/" + lr.getId())).body(lr);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable("id") String id, @RequestBody LeaveApprovalDto dto) {
        LeaveRequest lr = leaveService.approve(id, dto);
        return ResponseEntity.ok(lr);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable("id") String id, @RequestBody LeaveApprovalDto dto) {
        LeaveRequest lr = leaveService.reject(id, dto);
        return ResponseEntity.ok(lr);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> byEmployee(@PathVariable String employeeId) {
        List<LeaveRequest> list = leaveService.getRequestsForEmployee(employeeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> get(@PathVariable String id) {
        return ResponseEntity.ok(leaveService.getById(id));
    }
}
