package com.kaifan.emloyeeManagement.service;

import com.kaifan.emloyeeManagement.dto.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing employees.
 */
public interface EmployeeService {

    /**
     * Create a new employee.
     *
     * @param employeeDto the DTO containing employee details
     * @return the created employee DTO
     */
    EmployeeDto createEmployee(EmployeeDto employeeDto, MultipartFile photo);

    /**
     * Update an existing employee.
     *
     * @param employeeDto the DTO containing updated employee details
     * @return the updated employee DTO
     */
    EmployeeDto updateEmployee(EmployeeDto employeeDto);

    /**
     * Get employee by ID.
     *
     * @param id the ID of the employee to retrieve
     * @return the employee DTO
     */
    EmployeeDto getEmployeeById(String id);

    /**
     * Get all employees with pagination.
     *
     * @param pageable the pagination information
     * @return page of employee DTOs
     */
    Page<EmployeeDto> getAllEmployees(Pageable pageable);

    /**
     * Search employees by name (case-insensitive).
     *
     * @param name the name to search for
     * @param pageable the pagination information
     * @return page of matching employee DTOs
     */
    Page<EmployeeDto> searchEmployeesByName(String name, Pageable pageable);

    /**
     * Get employees by department.
     *
     * @param department the department name
     * @param pageable the pagination information
     * @return page of employee DTOs in the specified department
     */
    Page<EmployeeDto> getEmployeesByDepartment(String department, Pageable pageable);

    /**
     * Get employees by employment status.
     *
     * @param status the employment status
     * @param pageable the pagination information
     * @return page of employee DTOs with the specified status
     */
    Page<EmployeeDto> getEmployeesByStatus(String status, Pageable pageable);

    /**
     * Delete an employee by ID.
     *
     * @param id the ID of the employee to delete
     */
    void deleteEmployee(String id);

    /**
     * Get employees by manager.
     *
     * @param managerId the ID of the manager
     * @param pageable the pagination information
     * @return page of employee DTOs managed by the specified manager
     */
    Page<EmployeeDto> getEmployeesByManager(String managerId, Pageable pageable);

    EmployeeDto getEmployeeByAdUsername(String adUsername);

    EmployeeDto getEmployee();
}
