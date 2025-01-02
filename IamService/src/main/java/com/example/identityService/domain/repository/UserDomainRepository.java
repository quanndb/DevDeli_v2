package com.example.identityService.domain.repository;

import com.devdeli.common.DomainRepository;
import com.example.identityService.domain.User;

import java.util.List;
import java.util.UUID;

public interface UserDomainRepository extends DomainRepository<User, UUID> {
    boolean existsByEmail(String email);
    List<String> existedEmailsFromEmails(List<String> emails);
    User getByEmail(String email);
}
