package com.example.identityService.infrastructure.domainRepository;

import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.service.auth.AbstractAuthService;
import com.example.identityService.domain.User;
import com.example.identityService.domain.UserRole;
import com.example.identityService.domain.repository.UserDomainRepository;
import com.example.identityService.domain.repository.UserRoleDomainRepository;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
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

    @Override
    public Optional<User> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public User getById(UUID uuid) {
        AccountEntity foundAccount = accountRepository.findById(uuid)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        return getFromAccountEntity(foundAccount);
    }

    @Override
    public List<User> findAllByIds(List<UUID> uuids) {
        return List.of();
    }

    @Override
    public boolean save(User domain) {
        AbstractAuthService.createOrUpdateUser(domain);
        return userRoleDomainRepository.saveAll(domain.getRoles());
    }

    @Override
    public boolean saveAll(List<User> domains) {
        return false;
    }

    @Override
    public boolean delete(User domain) {
        return false;
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return false;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return accountRepository.existsById(uuid);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public User getByEmail(String email) {
        AccountEntity accountEntity = accountRepository.getByEmail(email);
        return getFromAccountEntity(accountEntity);
    }

    public User getFromAccountEntity(AccountEntity accountEntity) {
        List<UserRole> userRoles = userRoleDomainRepository.getAllByUserId(accountEntity.getId());
        return new User(accountEntity.getId(),
                accountEntity.getEmail(),
                accountEntity.getPassword(),
                accountEntity.getFullname(),
                accountEntity.getDob(),
                accountEntity.getYoe(),
                accountEntity.isVerified(),
                accountEntity.isEnable(),
                accountEntity.isDeleted(),
                accountEntity.getGender(),
                accountEntity.getAddress(),
                accountEntity.getCloudImageId(),
                accountEntity.getCloudImageUrl(),
                userRoles);
    }
}
