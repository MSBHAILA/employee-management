package com.kaifan.emloyeeManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String userId;

    private String username;

    private String firstName;

    private String lastName;

    private Boolean status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
