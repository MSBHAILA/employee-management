package com.kaifan.emloyeeManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDto {
    private String id;
    private String name;
    private Integer level;
    private String roleCode;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
