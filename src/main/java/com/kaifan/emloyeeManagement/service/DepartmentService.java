package com.kaifan.emloyeeManagement.service;

import com.kaifan.emloyeeManagement.dto.DepartmentDto;
import com.kaifan.emloyeeManagement.repository.interfaces.DepartmentResponseDao;

import java.util.List;

public interface DepartmentService {

    DepartmentDto createDepartment(DepartmentDto departmentDto);

    DepartmentDto getDepartmentById(String id);

    DepartmentDto updateDepartment(DepartmentDto departmentDto);

    void deleteDepartment(String id);

    List<DepartmentResponseDao> getAllDepartmentDetails();
}
