package com.example.identityService.service;

import com.example.identityService.DTO.Gender;
import com.example.identityService.DTO.request.CreateAccountRequest;
import com.example.identityService.DTO.request.UserPageRequest;
import com.example.identityService.DTO.response.PageResponse;
import com.example.identityService.DTO.response.UserResponse;
import com.example.identityService.Util.ExcelHelper;
import com.example.identityService.entity.Account;
import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.mapper.AccountMapper;
import com.example.identityService.repository.AccountRepository;
import com.example.identityService.service.auth.AbstractAuthService;
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
    private final StorageService storageService;

    public UserResponse getUserinfo(String accountId){
        Account foundAccount = accountRepository.findById(accountId)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        List<String> roles = accountRoleService.getAllUserRole(foundAccount.getId());
        UserResponse response = accountMapper.toUserResponse(foundAccount);
        response.setRoles(roles);
        return response;
    }

    public boolean setUserEnable(String accountId, boolean enable){
        Account foundAccount = accountRepository.findById(accountId)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        foundAccount.setEnable(enable);

        accountRepository.save(foundAccount);
        return true;
    }

    public boolean deleteUser(String accountId) {
        Account foundAccount = accountRepository.findById(accountId)
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
            String[] headers = {"STT", "Username", "Họ Tên", "Ngày sinh", "Địa chỉ",
                    "Số năm kinh nghiệm", "Giới tính", "Image url", "Image id",
                    "Ngày tạo", "Người tạo", "Đã xác thực", "Đang hoạt động", "Vai trò"};
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
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getFullname());
                row.createCell(3).setCellValue(user.getDob().toString());
                row.createCell(4).setCellValue(user.getAddress());
                row.createCell(5).setCellValue(user.getYoe());
                row.createCell(6).setCellValue(user.getGender() == null? null : user.getGender().name());
                row.createCell(7).setCellValue(user.getCloudImageUrl());
                row.createCell(8).setCellValue(user.getCloudImageId());
                row.createCell(9).setCellValue(user.getCreatedDate().toString());
                row.createCell(10).setCellValue(user.getCreatedBy());
                row.createCell(11).setCellValue(user.isVerified());
                row.createCell(12).setCellValue(user.isEnable());
                row.createCell(13).setCellValue(String.join(", ", accountRoleService.getAllUserRole(user.getId())));
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
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            throw new AppExceptions(ErrorCode.INVALID_FILE);
        }
        List<String> errors = new ArrayList<>();
        List<CreateAccountRequest> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1 (row 0 is header)
                Row row = sheet.getRow(i);
                try {
                    String email = ExcelHelper.getCellStringValue(row, 1);
                    String fullName = ExcelHelper.getCellStringValue(row, 2);
                    LocalDate birthDate = ExcelHelper.getCellDateValue(row, 3);
                    String address = ExcelHelper.getCellStringValue(row, 4);
                    int yearsOfExperience = ExcelHelper.getCellNumericValue(row, 5);
                    String gender = ExcelHelper.getCellStringValue(row, 6);
                    String cloudImageUrl = ExcelHelper.getCellStringValue(row, 7);
                    String cloudImageId = ExcelHelper.getCellStringValue(row, 8);
                    boolean verified = ExcelHelper.getCellBooleanValue(row, 11);
                    boolean enable = ExcelHelper.getCellBooleanValue(row, 12);
                    String roles = ExcelHelper.getCellStringValue(row, 13);


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
                                    .roles(List.of(roles.split(",\\s*")))
                            .build());

                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
            if(errors.isEmpty()){
                for(CreateAccountRequest item : users) AbstractAuthService.createUser(item);
            }
        }
        return errors;
    }
}
