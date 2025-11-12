package com.kaifan.emloyeeManagement.service.impl;

import com.kaifan.emloyeeManagement.dto.EmployeeDto;
import com.kaifan.emloyeeManagement.entity.Department;
import com.kaifan.emloyeeManagement.entity.Employee;
import com.kaifan.emloyeeManagement.mapper.EmployeeMapper;
import com.kaifan.emloyeeManagement.repository.DepartmentRepository;
import com.kaifan.emloyeeManagement.repository.EmployeeRepository;
import com.kaifan.emloyeeManagement.service.EmployeeService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeMapper employeeMapper;


    @Override
    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employeeDto, MultipartFile photo) {
        employeeDto.setEmployeeNumber(generateNextEmployeeNumber());

        // 1. Handle photo saving (e.g., file system, S3, etc.)
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = savePhoto(photo, employeeDto.getEmployeeNumber()); // Custom method
            employeeDto.setPhotoUrl(photoUrl);
        }

        // 2. Set manager based on department if department is provided
        if (employeeDto.getDepartment() != null && !employeeDto.getDepartment().isEmpty()) {
            setManagerFromDepartment(employeeDto);
        }

        // 3. Save employee
        Employee employee = employeeMapper.employeeDtoToEmployee(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeDto(savedEmployee);
    }

    private String savePhoto(MultipartFile photo, String employeeNumber) {
        try {
            String uploadsDir = "uploads/";
            String fileExtension = Objects.requireNonNull(photo.getOriginalFilename())
                    .substring(photo.getOriginalFilename().lastIndexOf('.'));
            String filename = "employee_" + employeeNumber + fileExtension;

            Path uploadPath = Paths.get(uploadsDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the path or URL
            return "/uploads/" + filename;  // Or full URL if hosted
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }


    private String generateNextEmployeeNumber() {
        // Get the current max number
        Optional<Employee> last = employeeRepository.findTopByOrderByEmployeeNumberDesc();

        int lastNumber = last.map(emp -> {
            try {
                return Integer.parseInt(emp.getEmployeeNumber());
            } catch (NumberFormatException e) {
                return 499; // fallback if format is invalid
            }
        }).orElse(499);

        return String.valueOf(lastNumber + 1);
    }

    @Override
    @Transactional
    public EmployeeDto getEmployeeById(String id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return employeeMapper.employeeToEmployeeDto(employee.get());
        }
        return null;
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        // Check if employee exists
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeDto.getId());
        if (existingEmployeeOpt.isEmpty()) {
            return null;
        }
        
        Employee existingEmployee = existingEmployeeOpt.get();
        
        // Check if department is being updated
        boolean departmentChanged = employeeDto.getDepartment() != null && 
                                  !employeeDto.getDepartment().equals(existingEmployee.getDepartment());
        
        // If department is changed or manager is not set, update manager from department
        if ((departmentChanged || employeeDto.getManagerId() == null) && 
            employeeDto.getDepartment() != null && !employeeDto.getDepartment().isEmpty()) {
            setManagerFromDepartment(employeeDto);
        }
        
        // Update the employee
        Employee employee = employeeMapper.employeeDtoToEmployee(employeeDto);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(String id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
        }
    }

    // Implementing other methods from the interface with default implementation
    @Override
    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<EmployeeDto> searchEmployeesByName(String name, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<EmployeeDto> getEmployeesByDepartment(String department, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<EmployeeDto> getEmployeesByStatus(String status, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<EmployeeDto> getEmployeesByManager(String managerId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Sets the manager for an employee based on their department's manager
     * @param employeeDto The employee DTO to update with the manager ID
     */
    private void setManagerFromDepartment(EmployeeDto employeeDto) {
        try {
            // Find the department by name
            Department department = departmentRepository.findByName(employeeDto.getDepartment());
            
            // If department exists and has a manager, set it as the employee's manager
            if (department != null && department.getManagerId() != null && !department.getManagerId().isEmpty()) {
                // Don't set the manager if the employee is the manager of their own department
                if (!department.getManagerId().equals(employeeDto.getId())) {
                    employeeDto.setManagerId(department.getManagerId());
                }
            }
        } catch (Exception e) {
            // Log the error but don't fail the operation
            // In a production environment, you might want to log this to a monitoring system
            System.err.println("Error setting manager from department: " + e.getMessage());
        }
    }
}