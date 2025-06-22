package com.kaifan.emloyeeManagement.repository.interfaces;

public interface DepartmentResponseDao {
    String getId();
    String getName();
    String getCode();
    String getParentDepartmentId();
    String getManagerId();
    String getDescription();
    Boolean getIsActive();
    String getManagerName();
}
