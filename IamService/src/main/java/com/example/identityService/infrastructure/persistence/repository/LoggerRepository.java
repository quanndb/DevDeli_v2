package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepository extends JpaRepository<LogEntity, String> {
    boolean existsByEmailAndIp(String email, String ip);
}
