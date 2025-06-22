package com.kaifan.emloyeeManagement.mapper;

import com.kaifan.emloyeeManagement.dto.DepartmentDto;
import com.kaifan.emloyeeManagement.entity.Department;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    /**
     * Convert Department entity to DepartmentDto
     * @param department the entity to convert
     * @return the converted DTO
     */
    DepartmentDto departmentToDepartmentDto(Department department);

    /**
     * Convert DepartmentDto to Department entity
     * @param departmentDto the DTO to convert
     * @return the converted entity
     */
    Department departmentDtoToDepartment(DepartmentDto departmentDto);

    /**
     * Convert list of Department entities to list of DepartmentDto
     * @param departments the list of entities to convert
     * @return the list of converted DTOs
     */
    @InheritConfiguration(name = "departmentToDepartmentDto")
    List<DepartmentDto> departmentsToDepartmentDtos(List<Department> departments);
}
