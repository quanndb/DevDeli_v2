package com.example.identityService.application.service;

import com.devdeli.common.dto.response.PageResponse;
import com.example.identityService.application.DTO.EnumCell;
import com.example.identityService.application.DTO.EnumSheetHeader;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountRoleService accountRoleService;

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
                row.createCell(EnumCell.CELL11.ordinal()).setCellValue(user.getVerified());
                row.createCell(EnumCell.CELL12.ordinal()).setCellValue(user.getEnable());
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
}
