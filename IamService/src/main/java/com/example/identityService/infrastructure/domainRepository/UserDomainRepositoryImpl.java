package com.example.identityService.infrastructure.domainRepository;

import com.example.identityService.domain.UserDomain;
import com.example.identityService.domain.repository.UserDomainRepository;
import com.example.identityService.domain.repository.UserRoleDomainRepository;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.infrastructure.persistence.mapper.AccountMapper;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDomainRepositoryImpl implements UserDomainRepository {

    private final AccountRepository accountRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final AccountMapper accountMapper;

    @Override
    public Optional<UserDomain> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public UserDomain getById(UUID uuid) {
        return null;
    }

    @Override
    public List<UserDomain> findAllByIds(List<UUID> uuids) {
        return List.of();
    }

    @Override
    public boolean save(UserDomain domain) {
        AccountEntity newAccount = accountMapper.toAccount(domain);
        accountRepository.save(newAccount);
        userRoleDomainRepository.saveAll(domain.getRoles());
        return false;
    }

    @Override
    public boolean saveAll(List<UserDomain> domains) {
        return false;
    }

    @Override
    public boolean delete(UserDomain domain) {
        return false;
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return false;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }
}
