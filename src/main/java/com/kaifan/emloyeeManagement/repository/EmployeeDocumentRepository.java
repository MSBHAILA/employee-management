package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, String> {
    

    List<EmployeeDocument> findByEmployeeId(String employeeId);

    List<EmployeeDocument> findByEmployeeIdAndDocumentType(String employeeId, String documentType);

    boolean existsByEmployeeIdAndFileName(String employeeId, String fileName);

    @Query("SELECT COUNT(d) FROM EmployeeDocument d WHERE d.employeeId = :employeeId")
    long countByEmployeeId(@Param("employeeId") String employeeId);
}
