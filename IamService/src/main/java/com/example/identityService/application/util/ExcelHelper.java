package com.example.identityService.application.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ExcelHelper {

    // Lấy giá trị chuỗi từ ô, trả về chuỗi rỗng nếu ô không tồn tại hoặc rỗng
    public static String getCellStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell != null ? cell.getStringCellValue().trim() : "";
    }

    // Lấy giá trị ngày từ ô, trả về giá trị mặc định nếu không thể chuyển đổi
    public static LocalDate getCellDateValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell != null && cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue().trim());
            } catch (DateTimeParseException e) {
                return LocalDate.now(); // Giá trị mặc định
            }
        }
        return LocalDate.now(); // Giá trị mặc định
    }

    // Lấy giá trị số từ ô, trả về giá trị mặc định nếu không phải kiểu số
    public static int getCellNumericValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return (cell != null && cell.getCellType() == CellType.NUMERIC) ? (int) cell.getNumericCellValue() : 0;
    }

    // Lấy giá trị boolean từ ô, trả về giá trị mặc định nếu ô không chứa boolean
    public static boolean getCellBooleanValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell != null && cell.getCellType() == CellType.BOOLEAN && cell.getBooleanCellValue();
    }
}
