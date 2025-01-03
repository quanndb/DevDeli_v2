package com.example.identityService.repository;

import com.example.identityService.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, CustomAccountRepository {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
}
