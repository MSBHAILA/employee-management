package com.kaifan.emloyeeManagement.repository;

import com.kaifan.emloyeeManagement.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {
    Optional<Position> findById(String id);
}
