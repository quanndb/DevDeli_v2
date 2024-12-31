package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID>, CustomAccountRepository {
    Optional<AccountEntity> findByEmail(String email);
    AccountEntity getById(UUID id);
    AccountEntity getByEmail(String email);
    boolean existsByEmail(String email);
}
