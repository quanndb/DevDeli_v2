package com.example.identityService.application.service.impl;

import com.devdeli.common.service.FileService;
import com.example.identityService.application.DTO.EnumCell;
import com.example.identityService.application.DTO.Gender;
import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.UpdateAccountRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.mapper.UserCommandMapper;
import com.example.identityService.application.service.UserCommandService;
import com.example.identityService.application.util.ExcelHelper;
import com.example.identityService.domain.User;
import com.example.identityService.domain.UserRole;
import com.example.identityService.domain.command.CreateUserCommand;
import com.example.identityService.domain.command.RegisterCommand;
import com.example.identityService.domain.command.UpdateUserCommand;
import com.example.identityService.domain.command.UpdateUserInfoCommand;
import com.example.identityService.domain.repository.UserDomainRepository;
import com.example.identityService.domain.repository.UserRoleDomainRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserCommandImpl implements UserCommandService {

    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final UserCommandMapper userCommandMapper;
    private final UserDomainRepository userDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;

    @Override
    @Transactional
    public boolean register(RegisterRequest request) {
        checkUserDoesNotExist(request.getEmail());

        RegisterCommand cmd = new RegisterCommand();
        userCommandMapper.fromCreateDtoToCreateCmd(cmd, request);
        User userDomain = new User(cmd);

        return userDomainRepository.save(userDomain);
    }

    @Override
    @Transactional
    public boolean createUser(CreateAccountRequest request) {
        checkUserDoesNotExist(request.getEmail());
        boolean validRoleIds = userRoleDomainRepository.existsIds(request.getRoleIds());
        if(!validRoleIds) throw new AppExceptions(ErrorCode.ROLE_NOTFOUND);

        CreateUserCommand cmd = new CreateUserCommand();
        userCommandMapper.fromCreateDtoToCreateCmd(cmd, request);
        User userDomain = new User(cmd);

        return userDomainRepository.save(userDomain);
    }

    @Override
    @Transactional
    public boolean updateUser(UUID userId, UpdateAccountRequest request) {
        checkUserExists(userId);
        boolean validRoleIds = userRoleDomainRepository.existsIds(request.getRoleIds());
        if(!validRoleIds) throw new AppExceptions(ErrorCode.ROLE_NOTFOUND);

        UpdateUserCommand cmd = new UpdateUserCommand();
        userCommandMapper.fromUpdateDtoToUpdateCmd(cmd, request);

        List<UserRole> userRoles = userRoleDomainRepository.getAllByUserId(userId);
        User foundDomain = userDomainRepository.getById(userId);
        foundDomain.updateUser(userRoles, cmd);

        return userDomainRepository.save(foundDomain);
    }

    @Override
    @Transactional
    public boolean updateUserInfo(String email, UpdateProfileRequest request) {
        checkUserExists(email);
        User foundDomain = userDomainRepository.getByEmail(email);

        UpdateUserInfoCommand cmd = new UpdateUserInfoCommand();
        userCommandMapper.fromUpdateDtoToUpdateCmd(cmd, request);
        foundDomain.updateUserInfo(cmd);
        return userDomainRepository.save(foundDomain);
    }

    @Override
    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        if(oldPassword.equals(newPassword)) throw new AppExceptions(ErrorCode.PASSWORD_MUST_DIFFERENCE);
        checkUserExists(email);

        User foundDomain = userDomainRepository.getByEmail(email);
        if(!passwordEncoder.matches(oldPassword, foundDomain.getPassword())) throw new AppExceptions(ErrorCode.WRONG_PASSWORD);
        foundDomain.upDatePassword(newPassword);
        return userDomainRepository.save(foundDomain);
    }

    @Override
    @Transactional
    public boolean resetPassword(String email, String newPassword) {
        checkUserExists(email);
        User foundDomain = userDomainRepository.getByEmail(email);
        foundDomain.upDatePassword(newPassword);
        return userDomainRepository.save(foundDomain);
    }

    @Override
    @Transactional
    public boolean setUserEnable(UUID userId, boolean enable) {
        checkUserExists(userId);
        User foundDomain = userDomainRepository.getById(userId);
        foundDomain.updateUserEnable(enable);
        return userDomainRepository.save(foundDomain);
    }

    @Override
    @Transactional
    public boolean deleteUser(UUID userId) {
        checkUserExists(userId);
        User foundDomain = userDomainRepository.getById(userId);
        foundDomain.deleteUser();
        return userDomainRepository.save(foundDomain);
    }

    @Override
    @Transactional
    public List<String> importUsers(MultipartFile file, String ownerId) throws IOException {
        List<String> errors = new ArrayList<>();
        List<CreateAccountRequest> requests = extractCreateAccountRequests(file, errors);
        List<String> existedEmails = userDomainRepository.existedEmailsFromEmails(requests
                .stream().map(CreateAccountRequest::getEmail).collect(Collectors.toList()));
        if(!existedEmails.isEmpty()) errors.addAll(existedEmails);
        if(errors.isEmpty()){
            List<User> users = new ArrayList<>();
            for(CreateAccountRequest request : requests){
                CreateUserCommand cmd = new CreateUserCommand();
                userCommandMapper.fromCreateDtoToCreateCmd(cmd, request);
                User userDomain = new User(cmd);
                users.add(userDomain);
            }
            userDomainRepository.saveAll(users);
            fileService.uploadPrivateFiles(List.of(file), ownerId);
        }
        return errors;
    }

    // utilities
    private void checkUserExists(String email) {
        boolean existedUser = userDomainRepository.existsByEmail(email);
        if (!existedUser) throw new AppExceptions(ErrorCode.NOTFOUND_EMAIL);
    }

    private void checkUserExists(UUID uuid) {
        boolean existedUser = userDomainRepository.existsById(uuid);
        if (!existedUser) throw new AppExceptions(ErrorCode.NOTFOUND_EMAIL);
    }

    private void checkUserDoesNotExist(String email) {
        boolean existedUser = userDomainRepository.existsByEmail(email);
        if (existedUser) throw new AppExceptions(ErrorCode.EMAIL_EXISTED);
    }

    private List<CreateAccountRequest> extractCreateAccountRequests(MultipartFile file, List<String> errors) throws IOException {
        if (file.isEmpty() ||
                !Objects.requireNonNull(file.getOriginalFilename()).matches(".*\\.xls(x?)$")) {
            throw new AppExceptions(ErrorCode.INVALID_FILE);
        }

        List<CreateAccountRequest> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1 (row 0 is header)
                Row row = sheet.getRow(i);
                try {
                    String email = ExcelHelper.getCellStringValue(row, EnumCell.CELL1.ordinal());
                    String fullName = ExcelHelper.getCellStringValue(row, EnumCell.CELL2.ordinal());
                    LocalDate birthDate = ExcelHelper.getCellDateValue(row, EnumCell.CELL3.ordinal());
                    String address = ExcelHelper.getCellStringValue(row, EnumCell.CELL4.ordinal());
                    int yearsOfExperience = ExcelHelper.getCellNumericValue(row, EnumCell.CELL5.ordinal());
                    String gender = ExcelHelper.getCellStringValue(row, EnumCell.CELL6.ordinal());
                    String cloudImageUrl = ExcelHelper.getCellStringValue(row, EnumCell.CELL7.ordinal());
                    String cloudImageId = ExcelHelper.getCellStringValue(row, EnumCell.CELL8.ordinal());
                    boolean verified = ExcelHelper.getCellBooleanValue(row, EnumCell.CELL11.ordinal());
                    boolean enable = ExcelHelper.getCellBooleanValue(row, EnumCell.CELL12.ordinal());
                    String roles = ExcelHelper.getCellStringValue(row, EnumCell.CELL13.ordinal());


                    if (email.isEmpty() || fullName.isEmpty()) {
                        throw new IllegalArgumentException("Email or Full Name cannot be empty.");
                    }

                    users.add(CreateAccountRequest.builder()
                            .email(email)
                            .fullname(fullName)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .dob(birthDate)
                            .address(address)
                            .yoe(yearsOfExperience)
                            .gender(Gender.valueOf(gender))
                            .cloudImageUrl(cloudImageUrl)
                            .cloudImageId(cloudImageId)
                            .verified(verified)
                            .enable(enable)
                            .roleIds(Arrays.stream(roles.split(",\\s*")).map(UUID::fromString).collect(Collectors.toList()))
                            .build());

                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
        return users;
    }
}
