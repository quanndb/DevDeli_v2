package com.example.identityService.application.service;

import com.devdeli.common.service.FileService;
import com.example.identityService.application.DTO.EnumCell;
import com.example.identityService.application.DTO.EnumSheetHeader;
import com.example.identityService.application.DTO.Gender;
import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.mapper.UserQueryMapper;
import com.example.identityService.application.util.ExcelHelper;
import com.example.identityService.domain.query.GetUserPageQuery;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.infrastructure.persistence.mapper.AccountMapper;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
import com.example.identityService.application.service.auth.AbstractAuthService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountRoleService accountRoleService;
    private final PasswordEncoder passwordEncoder;
    private final FileService remoteFileService;
    private final UserQueryMapper userQueryMapper;

    public UserResponse getUserinfo(UUID accountId){
        AccountEntity foundAccount = accountRepository.findById(accountId)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        List<String> roles = accountRoleService.getAllUserRole(foundAccount.getId());
        UserResponse response = accountMapper.toUserResponse(foundAccount);
        response.setRoles(roles);
        return response;
    }

    public boolean setUserEnable(UUID accountId, boolean enable){
        AccountEntity foundAccount = accountRepository.findById(accountId)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        foundAccount.setEnable(enable);

        accountRepository.save(foundAccount);
        return true;
    }

    public boolean deleteUser(UUID accountId) {
        AccountEntity foundAccount = accountRepository.findById(accountId)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        foundAccount.setDeleted(true);

        accountRepository.save(foundAccount);
        return true;
    }

    public PageResponse<UserResponse> getUsers(UserPageRequest request) {
        long totalRecords = accountRepository.count(request);
        List<UserResponse> userResponseList = accountRepository.search(request);
        return PageResponse.<UserResponse>builder()
                .page(request.getPage())
                .size(request.getSize())
                .query(request.getQuery())
                .sortedBy(request.getSortedBy())
                .sortDirection(request.getSortDirection().name())
                .first(request.getPage() == 1)
                .last(request.getPage() % request.getSize() == request.getPage())
                .totalRecords(totalRecords)
                .totalPages(request.getPage() % request.getSize())
                .response(userResponseList)
                .build();
    }

    public PageResponse<UserResponse> getUsers(GetUserPageQuery request) {
        UserPageRequest rq = userQueryMapper.toUserPageQuery(request);
        return getUsers(rq);
    }

    public byte[] exportUsers(UserPageRequest request) throws IOException {
        List<UserResponse> users = accountRepository.search(request); // Lọc dữ liệu theo filters

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("Users");
            Font font = workbook.createFont();
            font.setFontName("Times New Roman");

            CellStyle style = workbook.createCellStyle();
            style.setFont(font);

            // Create Header Row
            Row header = sheet.createRow(0);
            String[] headers = {EnumSheetHeader.STT.getHeader(), EnumSheetHeader.USERNAME.getHeader(), EnumSheetHeader.FULL_NAME.getHeader(),
                    EnumSheetHeader.DATE_OF_BIRTH.getHeader(), EnumSheetHeader.ADDRESS.getHeader(),
                    EnumSheetHeader.YEARS_OF_EXPERIENCE.getHeader(), EnumSheetHeader.GENDER.getHeader(),
                    EnumSheetHeader.IMAGE_URL.getHeader(), EnumSheetHeader.IMAGE_ID.getHeader(),
                    EnumSheetHeader.CREATED_DATE.getHeader(), EnumSheetHeader.CREATED_BY.getHeader(),
                    EnumSheetHeader.VERIFIED.getHeader(), EnumSheetHeader.ACTIVE.getHeader(), EnumSheetHeader.ROLE.getHeader()};
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontName("Times New Roman");
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(headerFont);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            // Fill Data
            int rowIdx = 1;
            for (UserResponse user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(EnumCell.CELL0.ordinal()).setCellValue(rowIdx - 1);
                row.createCell(EnumCell.CELL1.ordinal()).setCellValue(user.getEmail());
                row.createCell(EnumCell.CELL2.ordinal()).setCellValue(user.getFullname());
                row.createCell(EnumCell.CELL3.ordinal()).setCellValue(user.getDob().toString());
                row.createCell(EnumCell.CELL4.ordinal()).setCellValue(user.getAddress());
                row.createCell(EnumCell.CELL5.ordinal()).setCellValue(user.getYoe());
                row.createCell(EnumCell.CELL6.ordinal()).setCellValue(user.getGender() == null? null : user.getGender().name());
                row.createCell(EnumCell.CELL7.ordinal()).setCellValue(user.getCloudImageUrl());
                row.createCell(EnumCell.CELL8.ordinal()).setCellValue(user.getCloudImageId());
                row.createCell(EnumCell.CELL9.ordinal()).setCellValue(user.getCreatedDate().toString());
                row.createCell(EnumCell.CELL10.ordinal()).setCellValue(user.getCreatedBy());
                row.createCell(EnumCell.CELL11.ordinal()).setCellValue(user.isVerified());
                row.createCell(EnumCell.CELL12.ordinal()).setCellValue(user.isEnable());
                row.createCell(EnumCell.CELL13.ordinal()).setCellValue(String.join(", ", accountRoleService.getAllUserRole(user.getId())));
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(style);
                }
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public List<String> importUsers(MultipartFile file) throws IOException {
        if (file.isEmpty() ||
                !Objects.requireNonNull(file.getOriginalFilename()).matches(".*\\.xls(x?)$")) {
            throw new AppExceptions(ErrorCode.INVALID_FILE);
        }

        List<String> errors = new ArrayList<>();
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

                    if (accountRepository.existsByEmail(email)) {
                        throw new IllegalArgumentException("Email "+email+" already exists.");
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
//                                    .rolesIds(List.of(roles.split(",\\s*")))
                            .build());

                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
            if(errors.isEmpty()){
                for(CreateAccountRequest item : users) AbstractAuthService.createUser(item);
                remoteFileService.uploadPrivateFiles(List.of(file), SecurityContextHolder.getContext().getAuthentication().getName());
            }
        }
        return errors;
    }
}
