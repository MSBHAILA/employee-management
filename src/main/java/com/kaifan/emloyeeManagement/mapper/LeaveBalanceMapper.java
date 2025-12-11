package com.kaifan.emloyeeManagement.mapper;

import com.kaifan.emloyeeManagement.dto.LeaveBalanceDto;
import com.kaifan.emloyeeManagement.entity.LeaveBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeaveBalanceMapper {

    LeaveBalanceMapper INSTANCE = Mappers.getMapper(LeaveBalanceMapper.class);

    /**
     * Maps LeaveBalance entity to LeaveBalanceDto
     * @param leaveBalance the LeaveBalance entity to convert
     * @return the converted LeaveBalanceDto
     */
    @Mapping(target = "leaveType", source = "leaveType.name")
    @Mapping(target = "employee", expression = "java(leaveBalance.getEmployee().getId())")
    @Mapping(target = "year", source = "year", defaultValue = "0")
    @Mapping(target = "totalDays", source = "totalDays", defaultValue = "0")
    @Mapping(target = "usedDays", source = "usedDays", defaultValue = "0")
    @Mapping(target = "remainingDays", source = "remainingDays", defaultValue = "0")
    @Mapping(target = "reservedDays", source = "reservedDays", defaultValue = "0")
    @Mapping(target = "carriedForwardDays", source = "carriedForwardDays", defaultValue = "0")
    LeaveBalanceDto toDto(LeaveBalance leaveBalance);

    /**
     * Maps List of LeaveBalance entities to List of LeaveBalanceDto
     * @param leaveBalances the list of LeaveBalance entities to convert
     * @return the list of converted LeaveBalanceDto objects
     */
    List<LeaveBalanceDto> toDtoList(List<LeaveBalance> leaveBalances);

    /**
     * Maps LeaveBalanceDto to LeaveBalance entity
     * @param dto the LeaveBalanceDto to convert
     * @return the converted LeaveBalance entity
     */
    @Mapping(target = "leaveType", ignore = true) // Will be set separately in service
    @Mapping(target = "employee", ignore = true)  // Will be set separately in service
    @Mapping(target = "year", source = "year", defaultValue = "0")
    @Mapping(target = "totalDays", source = "totalDays", defaultValue = "0")
    @Mapping(target = "usedDays", source = "usedDays", defaultValue = "0")
    @Mapping(target = "remainingDays", source = "remainingDays", defaultValue = "0")
    @Mapping(target = "reservedDays", source = "reservedDays", defaultValue = "0")
    @Mapping(target = "carriedForwardDays", source = "carriedForwardDays", defaultValue = "0")
    LeaveBalance toEntity(LeaveBalanceDto dto);

    /**
     * Maps List of LeaveBalanceDto to List of LeaveBalance entities
     * @param dtos the list of LeaveBalanceDto to convert
     * @return the list of converted LeaveBalance entities
     */
    List<LeaveBalance> toEntityList(List<LeaveBalanceDto> dtos);
}
