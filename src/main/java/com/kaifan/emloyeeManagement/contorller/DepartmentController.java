package com.kaifan.emloyeeManagement.contorller;

import com.kaifan.emloyeeManagement.constants.MessageConstants;
import com.kaifan.emloyeeManagement.dto.DepartmentDto;
import com.kaifan.emloyeeManagement.dto.ResponseDto;
import com.kaifan.emloyeeManagement.repository.interfaces.DepartmentResponseDao;
import com.kaifan.emloyeeManagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing departments.
 */
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * Create a new department.
     *
     * @param departmentDto the department to create
     * @return the created department
     */
    @PostMapping
    public ResponseDto createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        if (createdDepartment != null) {
            return new ResponseDto(true, createdDepartment, MessageConstants.RECORD_CREATED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_ALREADY_EXISTS);
    }

    /**
     * Get a department by ID.
     *
     * @param id the ID of the department to retrieve
     * @return the department
     */
    @GetMapping("/{id}")
    public ResponseDto getDepartmentById(@PathVariable String id) {
        DepartmentDto departmentDto = departmentService.getDepartmentById(id);
        if (departmentDto != null) {
            return new ResponseDto(true, departmentDto, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_NOT_FOUND);
    }

    /**
     * Update a department.
     *
     * @param id            the ID of the department to update
     * @param departmentDto the department details to update
     * @return the updated department
     */
    @PutMapping("/{id}")
    public ResponseDto updateDepartment(@PathVariable String id, @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(departmentDto);
        if (updatedDepartment != null) {
            return new ResponseDto(true, updatedDepartment, MessageConstants.RECORD_UPDATED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_NOT_FOUND);
    }

    /**
     * Delete a department.
     *
     * @param id the ID of the department to delete
     * @return a response indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseDto deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
        return new ResponseDto(true, null, MessageConstants.RECORD_DELETED_SUCCESSFULLY);
    }

    @GetMapping("/all")
    public ResponseDto getAllDepartmentDetails() {
        List<DepartmentResponseDao> departmentDtos = departmentService.getAllDepartmentDetails();
        if (departmentDtos != null) {
            return new ResponseDto(true, departmentDtos, MessageConstants.RECORD_RETRIEVED_SUCCESSFULLY);
        }
        return new ResponseDto(false, null, MessageConstants.RECORD_NOT_FOUND);
    }
}
