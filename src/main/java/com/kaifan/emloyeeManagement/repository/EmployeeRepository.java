package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findTopByOrderByEmployeeNumberDesc();

//    List<Employee> findByDepartmentId(String departmentId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE employee e SET e.manager_id = :managerId WHERE e.department = :departmentName", nativeQuery = true)
    void updateManagerForDepartment(@Param("departmentName") String departmentName, @Param("managerId") String managerId);

}
