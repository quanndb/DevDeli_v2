package com.example.identityService.infrastructure.persistence.repository;

import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID>, CustomAccountRepository {
    Optional<AccountEntity> findByEmail(String email);

    @Query(value = "SELECT a.email FROM Account a WHERE a.email IN (:emails)", nativeQuery = true)
    List<String> existedEmailsInEmails(List<String> emails);
    AccountEntity getById(UUID id);
    List<AccountEntity> getByIdIn(List<UUID> ids);
    AccountEntity getByEmail(String email);
    boolean existsByEmail(String email);
}
