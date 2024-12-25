package com.example.identityService.domain.repository;

import com.example.identityService.domain.entity.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepository extends JpaRepository<Logs, String> {
    boolean existsByEmailAndIp(String email, String ip);
}
