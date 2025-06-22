package com.kaifan.emloyeeManagement.service.impl;

import com.kaifan.emloyeeManagement.dto.EmployeeDto;
import com.kaifan.emloyeeManagement.entity.Employee;
import com.kaifan.emloyeeManagement.mapper.EmployeeMapper;
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

        // 2. Save employee
        Employee savedEmployee = employeeRepository.save(employeeMapper.employeeDtoToEmployee(employeeDto));
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
        if (!employeeRepository.existsById(employeeDto.getId())) {
            return null;
        }
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
}