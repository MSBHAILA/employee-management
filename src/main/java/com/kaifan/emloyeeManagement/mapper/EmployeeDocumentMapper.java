package com.kaifan.emloyeeManagement.mapper;

import com.kaifan.emloyeeManagement.dto.EmployeeDocumentDto;
import com.kaifan.emloyeeManagement.entity.EmployeeDocument;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeDocumentMapper {

    /**
     * Convert EmployeeDocument entity to EmployeeDocumentDto
     * @param employeeDocument the entity to convert
     * @return the converted DTO
     */
    EmployeeDocumentDto employeeDocumentToEmployeeDocumentDto(EmployeeDocument employeeDocument);

    /**
     * Convert EmployeeDocumentDto to EmployeeDocument entity
     * @param employeeDocumentDto the DTO to convert
     * @return the converted entity
     */
    EmployeeDocument employeeDocumentDtoToEmployeeDocument(EmployeeDocumentDto employeeDocumentDto);

    /**
     * Convert list of EmployeeDocument entities to list of EmployeeDocumentDto
     * @param employeeDocuments the list of entities to convert
     * @return the list of converted DTOs
     */
    @InheritConfiguration(name = "employeeDocumentToEmployeeDocumentDto")
    List<EmployeeDocumentDto> employeeDocumentsToEmployeeDocumentDtos(List<EmployeeDocument> employeeDocuments);

    /**
     * Convert list of EmployeeDocumentDto to list of EmployeeDocument entities
     * @param employeeDocumentDtos the list of DTOs to convert
     * @return the list of converted entities
     */
    @InheritConfiguration(name = "employeeDocumentDtoToEmployeeDocument")
    List<EmployeeDocument> employeeDocumentDtosToEmployeeDocuments(List<EmployeeDocumentDto> employeeDocumentDtos);
}
