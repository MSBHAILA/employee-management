package com.kaifan.emloyeeManagement.service.impl;

import com.kaifan.emloyeeManagement.dto.DepartmentDto;
import com.kaifan.emloyeeManagement.dto.EmployeeDto;
import com.kaifan.emloyeeManagement.entity.Department;
import com.kaifan.emloyeeManagement.mapper.DepartmentMapper;
import com.kaifan.emloyeeManagement.mapper.EmployeeMapper;
import com.kaifan.emloyeeManagement.repository.DepartmentRepository;
import com.kaifan.emloyeeManagement.repository.EmployeeRepository;
import com.kaifan.emloyeeManagement.repository.interfaces.DepartmentResponseDao;
import com.kaifan.emloyeeManagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = departmentMapper.departmentDtoToDepartment(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.departmentToDepartmentDto(savedDepartment);
    }

    @Override
    public DepartmentDto getDepartmentById(String id) {
        Department department = departmentRepository.findById(id).orElse(null);
        if(department != null) {
            return departmentMapper.departmentToDepartmentDto(department);
        }
        return null;
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(DepartmentDto departmentDto) {
        Department existingDepartment = departmentRepository.findById(departmentDto.getId()).orElse(null);
        if(null != existingDepartment) {
//            If manager is changed for existing department, then update the manager for all employees in that department
            if(!departmentDto.getManagerId().equals(existingDepartment.getManagerId())) {
                employeeRepository.updateManagerForDepartment(existingDepartment.getName(), departmentDto.getManagerId());
            }
            return departmentMapper.departmentToDepartmentDto(
                    departmentRepository.save(departmentMapper.departmentDtoToDepartment(departmentDto)));
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteDepartment(String id) {
        Department department = departmentRepository.findById(id).orElse(null);
        if (department != null) {
            departmentRepository.delete(department);
        }
    }

    @Override
    public List<DepartmentResponseDao> getAllDepartmentDetails() {
        return departmentRepository.getAllDepartmentDetails();
    }
}
