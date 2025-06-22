package com.kaifan.emloyeeManagement.mapper;

import com.kaifan.emloyeeManagement.dto.EmployeeDto;
import com.kaifan.emloyeeManagement.entity.Employee;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    /**
     * Convert Employee entity to EmployeeDto
     * @param employee the entity to convert
     * @return the converted DTO
     */
    EmployeeDto employeeToEmployeeDto(Employee employee);

    /**
     * Convert EmployeeDto to Employee entity
     * @param employeeDto the DTO to convert
     * @return the converted entity
     */
    Employee employeeDtoToEmployee(EmployeeDto employeeDto);

//    /**
//     * Update Employee entity from EmployeeDto
//     * @param employeeDto the DTO with updated fields
//     * @param employee the entity to update
//     */
//    @Mapping(target = "id", ignore = true)
//    void updateEmployeeFromDto(EmployeeDto employeeDto, @MappingTarget Employee employee);

    /**
     * Convert list of Employee entities to list of EmployeeDto
     * @param employees the list of entities to convert
     * @return the list of converted DTOs
     */
    @InheritConfiguration(name = "employeeToEmployeeDto")
    List<EmployeeDto> employeesToEmployeeDtos(List<Employee> employees);

    /**
     * Convert list of EmployeeDto to list of Employee entities
     * @param employeeDtos the list of DTOs to convert
     * @return the list of converted entities
     */
    @InheritConfiguration(name = "employeeDtoToEmployee")
    List<Employee> employeeDtosToEmployees(List<EmployeeDto> employeeDtos);
}
