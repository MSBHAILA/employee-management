package com.kaifan.emloyeeManagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaifan.emloyeeManagement.constants.MessageConstants;
import com.kaifan.emloyeeManagement.dto.EmployeeDto;
import com.kaifan.emloyeeManagement.dto.ResponseDto;
import com.kaifan.emloyeeManagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * REST controller for managing employees.
 */
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create a new employee.
     *
     * @param employeeDto the employee to create
     * @return the created employee
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto createEmployee(
            @RequestPart("employeeDto") EmployeeDto employeeDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws JsonProcessingException {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto, photo);
        if (createdEmployee != null) {
            return new ResponseDto(true, createdEmployee, MessageConstants.RECORD_CREATED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_ALREADY_EXISTS);
    }

    @GetMapping("/me")
    public ResponseDto getEmployee() {
        EmployeeDto employeeDto = employeeService.getEmployee();
        if (employeeDto != null) {
            return new ResponseDto(true, employeeDto, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
    }


    /**
     * Get an employee by ID.
     *
     * @param id the ID of the employee to retrieve
     * @return the employee
     */
    @GetMapping("/{id}")
    public ResponseDto getEmployeeById(@PathVariable String id) {
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);
        if (employeeDto != null) {
            return new ResponseDto(true, employeeDto, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
    }

    @GetMapping("/adUsername/{adUsername}")
    public ResponseDto getEmployeeByAdUsername(@PathVariable String adUsername) {
        EmployeeDto employeeDto = employeeService.getEmployeeByAdUsername(adUsername);
        if (employeeDto != null) {
            return new ResponseDto(true, employeeDto, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
    }


    /**
     * Update an existing employee.
     *
     * @param employeeDto the updated employee data
     * @return the updated employee
     */
    @PutMapping
    public ResponseDto updateEmployee(@RequestBody EmployeeDto employeeDto) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto);
        if (updatedEmployee != null) {
            return new ResponseDto(true, updatedEmployee, MessageConstants.RECORD_UPDATED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_NOT_FOUND);
    }

    /**
     * Delete an employee by ID.
     *
     * @param id the ID of the employee to delete
     * @return a response indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseDto deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return new ResponseDto(true, null, MessageConstants.RECORD_DELETED_SUCCESSFULLY);
    }

    @GetMapping("/photo/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(CacheControl.maxAge(10, TimeUnit.HOURS).cachePublic())
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
