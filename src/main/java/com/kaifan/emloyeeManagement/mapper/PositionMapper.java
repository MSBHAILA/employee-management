package com.kaifan.emloyeeManagement.mapper;

import com.kaifan.emloyeeManagement.dto.PositionDto;
import com.kaifan.emloyeeManagement.entity.Position;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    
    PositionDto positionToPositionDto(Position position);

    Position positionDtoToPosition(PositionDto positionDto);
}
