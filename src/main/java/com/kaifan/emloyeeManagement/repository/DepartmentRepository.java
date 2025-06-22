package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.Department;
import com.kaifan.emloyeeManagement.repository.interfaces.DepartmentResponseDao;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    Department findByName(String name);

    @Query(value = """
            select
            	d.department_id as id,
            	d.name as name,
            	d.code as code,
            	d.parent_department_id as parentDepartmentId,
            	d.manager_id as managerId,
            	d.description as description,
            	d.is_active as isActive,
            	e.full_name_ar as managerName
            from
            	department d
            left join employee e on
            	e.manager_id = d.manager_id""", nativeQuery = true)
    List<DepartmentResponseDao> getAllDepartmentDetails();

    @Query(value = """
            select
            	d.department_id as id,
            	d.name as name,
            	d.code as code,
            	d.parent_department_id as parentDepartmentId,
            	d.manager_id as managerId,
            	d.description as description,
            	d.is_active as isActive,
            	e.full_name_ar as managerName
            from
            	department d
            left join employee e on
            	e.manager_id = d.manager_id
            where
            	d.code = :code""", nativeQuery = true)
    DepartmentResponseDao findDepartmentByCode(String code);

    Department findByCode(String code);
}
