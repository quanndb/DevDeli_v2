package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String>, CustomAccountRepository {
    Optional<AccountEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
